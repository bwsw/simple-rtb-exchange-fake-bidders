package com.bitworks.rtb.fake.bidder

import java.net.URI

import com.sun.net.httpserver.HttpExchange

/**
  * Thread of [[com.bitworks.rtb.fake.bidder.FakeBidder FakeBidder]]'s http server.
  *
  * @param httpExchange describe http connection
  * @author Pavel Tomskikh
  */
class FakeBidderThread(httpExchange: HttpExchange) extends Runnable {

  val factory = new ResponseFactory

  def run() {
    Thread.sleep(getTimeout(httpExchange.getRequestURI))
    try {
      val bidResponse = factory.createBidResponse(httpExchange.getRequestBody)
      sendResponse(httpExchange, bidResponse)
    }
    catch {
      case _: Throwable => httpExchange.sendResponseHeaders(204, -1)
    }
  }

  private def sendResponse(httpExchange: HttpExchange, bidResponse: Array[Byte]) {
    httpExchange.sendResponseHeaders(200, bidResponse.length)
    val outputStream = httpExchange.getResponseBody
    outputStream.write(bidResponse)
    outputStream.close()
  }

  private def getTimeout(url: URI): Int = {
    val params = url.getQuery
    if (params == null || params.isEmpty) 0
    else {
      val timeoutStringPrefix = "timeout="
      params.split('&').find(_.startsWith(timeoutStringPrefix)) match {
        case Some(s) => s.substring(timeoutStringPrefix.length).toInt
        case None => 0
      }
    }
  }
}
