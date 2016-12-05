package com.bitworks.rtb.fake.bidder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

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

  val factory = new ResponseFactory

  /**
    * Returns timeout.
    *
    * @param timeoutStr some timeout param from query string
    */
  def getTimeout(timeoutStr: Option[String]) = {
    timeoutStr match {
      case Some(ts) =>
        Try(ts.toLong) match {
          case Success(t) => t
          case Failure(_) => throw new IllegalArgumentException
        }
      case None => 0L
    }
  }

  /**
    * Returns price.
    *
    * @param priceStr some bid price from query string
    */
  def getPrice(priceStr: Option[String]) = {
    priceStr match {
      case Some(pr) =>
        Try(BigDecimal(pr)) match {
          case Success(p) => Some(p)
          case Failure(_) => throw new IllegalArgumentException
        }
      case None => None
    }
  }

  val route =
    post {
        parameters('timeout.?, 'price.?) { (timeoutStr, priceStr) =>
          entity(as[Array[Byte]]) { body =>
            val f = Future {
              val timeout = getTimeout(timeoutStr)
              val price = getPrice(priceStr)
              Thread.sleep(timeout)
              factory.createBidResponse(body, price)
            }
            onComplete(f) {
              case Success(bytes) => complete {
                HttpResponse(
                  entity = HttpEntity(
                    ContentType(MediaTypes.`application/json`), bytes))
              }
              case Failure(t) =>
                t.printStackTrace()
                complete(HttpResponse(status = StatusCodes.BadRequest))
            }
          }
        }
    }

  def run() = {
    Http().bindAndHandle(route, "0.0.0.0", config.port)
  }
}
