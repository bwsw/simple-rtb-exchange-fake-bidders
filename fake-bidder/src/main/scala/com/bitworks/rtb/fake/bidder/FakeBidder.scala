package com.bitworks.rtb.fake.bidder

/**
  * Entry point of fake bidder application.
  *
  * @author Pavel Tomskikh
  */
object FakeBidder extends App {

  ArgsParser(args) match {
    case Some(config) =>
      val handler = new RequestHandler(config)
      handler.run()
      println(s"Server online at http://${config.host}:${config.port}/")
      
    case None =>
  }
}
