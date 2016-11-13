package com.bitworks.rtb.fake_bidder

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
  }

  def apply(args: Seq[String]) = parser.parse(args, Config(0))
}
