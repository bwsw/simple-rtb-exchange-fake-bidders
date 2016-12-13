package com.bitworks.rtb.fake.bidder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config._


import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Request handler.
  *
  * @author Egor Ilchenko
  */
class RequestHandler(config: Config) {
  val akkaConf = ConfigFactory.load()
    .withValue("akka.loglevel", ConfigValueFactory.fromAnyRef("OFF"))
    .withValue("akka.stdout-loglevel", ConfigValueFactory.fromAnyRef("OFF"))

  implicit val system = ActorSystem("main", akkaConf)
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

  /**
    * Returns bid response modifier.
    *
    * @param modifierStr some modifier from query string
    */
  def getModifier(modifierStr: Option[String]) = {
    modifierStr match {
      case Some("invalidjson") => Some(InvalidJson)
      case Some("invaliddata") => Some(InvalidData)
      case Some("nobidnocontent") => Some(NoBidNoContent)
      case Some("nobidemptyjson") => Some(NoBidEmptyJson)
      case Some("nobidemptyseatbid") => Some(NoBidEmptySeatBid)
      case Some(str) => throw new IllegalArgumentException(s"$str is not valid modifier")
      case None => None
    }
  }

  val route =
    post {
      parameters('timeout.?, 'price.?, 'modifier.?) { (timeoutStr, priceStr, modifierStr) =>
        entity(as[Array[Byte]]) { body =>
          val modifier = getModifier(modifierStr)
          val timeout = getTimeout(timeoutStr)
          val price = getPrice(priceStr)
          val f = Future {
            Thread.sleep(timeout)
            factory.createBidResponse(body, price, modifier)
          }
          onComplete(f) {
            case Success(bytes) =>
              val status = if (modifier.contains(NoBidNoContent)) StatusCodes.NoContent else StatusCodes.OK
              complete {
                HttpResponse(
                  status = status,
                  entity = HttpEntity(
                    ContentType(MediaTypes.`application/json`), bytes))
              }
            case Failure(t) =>
              complete(HttpResponse(status = StatusCodes.BadRequest))
          }
        }
      }
    }

  def run() = {
    Http().bindAndHandle(route, config.host, config.port)
  }
}
