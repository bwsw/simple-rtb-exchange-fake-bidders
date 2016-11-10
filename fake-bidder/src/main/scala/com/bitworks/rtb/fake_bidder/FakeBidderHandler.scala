package com.bitworks.rtb.fake_bidder

import com.sun.net.httpserver.{HttpExchange, HttpHandler}


/**
  * Handler for [[com.bitworks.rtb.fake_bidder.FakeBidder FakeBidder]]'s http server.
  *
  * @author Pavel Tomskikh
  */
class FakeBidderHandler extends HttpHandler {

  def handle(httpExchange: HttpExchange) {
    new Thread(new FakeBidderThread(httpExchange)).start()
  }
}
