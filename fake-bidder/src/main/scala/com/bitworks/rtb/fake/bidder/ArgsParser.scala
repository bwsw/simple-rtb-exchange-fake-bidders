package com.bitworks.rtb.fake.bidder

/**
  * A parser for command line arguments.
  *
  * @author Pavel Tomskikh
  */
object ArgsParser {
  val parser = new scopt.OptionParser[Config]("fake-bidder") {
    opt[Int]('p', "port")
      .action((i, c) => c.copy(port = i))
      .required()
      .text("fake bidder's port")

    opt[String]('h', "host")
      .action((i, c) => c.copy(host = i))
      .required()
      .text("fake bidder's host")
  }

  def apply(args: Seq[String]) = parser.parse(args, Config(0, ""))
}
