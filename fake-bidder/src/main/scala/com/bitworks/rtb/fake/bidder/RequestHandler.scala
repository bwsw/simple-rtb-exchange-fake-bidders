package com.bitworks.rtb.fake.bidder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.collection.immutable.HashMap
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Request handler.
  *
  * @author Egor Ilchenko
  */
class RequestHandler(config: Config) {
  implicit val system = ActorSystem("main")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  var timeouts = new HashMap[String, Long]()
  val factory = new ResponseFactory

  val route =
    post {
      pathPrefix("timeout" / Segment) { timeoutName =>
        entity(as[String]) { timeoutValue =>
          Try(timeoutValue.toLong) match {
            case Success(t) =>
              timeouts = timeouts + (timeoutName -> t)
              complete("ok")
            case Failure(_) =>
              complete(s"failed, cannot parse $timeoutValue")
          }
        }
      } ~
        parameter('timeout.?) { timeoutStr =>
          entity(as[Array[Byte]]) { body =>
            val timeout =
              timeoutStr match {
                case Some(ts) =>
                  Try(ts.toLong) match {
                    case Success(t) => t
                    case Failure(_) => timeouts.get(ts) match {
                      case Some(v) => v
                      case None =>
                        timeouts = timeouts + (ts -> 0)
                        0L
                    }
                  }
                case None => 0L
              }

            val f = Future {
              Thread.sleep(timeout)
              factory.createBidResponse(body)
            }
            onComplete(f) {
              case Success(bytes) => complete(bytes)
              case Failure(t) => complete(t)
            }
          }
        }
    } ~
      get {
        path("timeouts") {
          complete {
            timeouts.map(x => s"${x._1} - ${x._2} ms").mkString("\n")
          }
        }
      }

  def run() = {
    Http().bindAndHandle(route, "0.0.0.0", config.port)
  }
}
