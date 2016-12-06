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
  val modifierFactory = new ResponseModifierFactory

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
      parameters('timeout.?, 'price.?, 'modifier.?, 'winhost.?) {
        (timeoutStr, priceStr, modifierStr, winHostStr) =>
          entity(as[Array[Byte]]) { body =>
            val modifier = modifierFactory.getModifier(modifierStr)
            modifier match {
              case Some(WinNoticeWithAdm |
                        WinNoticeWithoutAdm |
                        BrokenWinNoticeWithAdm |
                        TimeoutWinNoticeWithAdm(_)) if winHostStr.isEmpty =>
                complete {
                  HttpResponse(
                    entity = "win host not specified!",
                    status = StatusCodes.BadRequest)
                }
              case _ =>
                val timeout = getTimeout(timeoutStr)
                val price = getPrice(priceStr)
                val f = Future {
                  Thread.sleep(timeout)
                  factory.createBidResponse(body, price, modifier, winHostStr)
                }
                onComplete(f) {
                  case Success(bytes) =>
                    val status = if ( modifier.contains(NoBidNoContent) ) StatusCodes.NoContent else StatusCodes.OK
                    complete {
                      HttpResponse(
                        status = status,
                        entity = HttpEntity(
                          ContentType(MediaTypes.`application/json`), bytes))
                    }
                  case Failure(t) =>
                    t.printStackTrace()
                    complete(HttpResponse(status = StatusCodes.InternalServerError))
                }
            }
          }
      }
    } ~
      get {
        path("win-notice") {
          parameters('modifier, 'type) { (modifierStr, typeStr) =>
            val modifier = modifierFactory.getModifier(Some(modifierStr))
            val f = Future {
              modifier match {
                case Some(TimeoutWinNoticeWithAdm(timeout)) => Thread.sleep(timeout)
                case _ =>
              }
              factory.getAdm(typeStr, modifier)
            }
            onComplete(f) {
              case Success(body) =>
                complete(body)
              case Failure(t) =>
                t.printStackTrace()
                complete(HttpResponse(status = StatusCodes.InternalServerError))
            }
          }
        }
      }

  def run() = {
    Http().bindAndHandle(route, "0.0.0.0", config.port)
  }
}
