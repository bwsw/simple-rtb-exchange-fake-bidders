package com.bitworks.rtb.fake.bidder

import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpServer

/**
  * Entry point of fake bidder application.
  *
  * @author Pavel Tomskikh
  */
object FakeBidder extends App {

  ArgsParser(args) match {
    case Some(config) =>
      val server = HttpServer.create(new InetSocketAddress(config.port), 0)
      server.createContext("/", new FakeBidderHandler())
      server.setExecutor(null)

      server.start()

      println("Press any key to exit")

      System.in.read()
      server.stop(0)

    case None =>
  }
}
