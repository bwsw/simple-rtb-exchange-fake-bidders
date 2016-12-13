package com.bitworks.rtb.fake.bidder

/**
  * Modifier for bid response.
  *
  * @author Egor Ilchenko
  */
trait ResponseModifier {
  val name: String
}

/**
  * Generate response with invalid JSON syntax.
  */
case object InvalidJson extends ResponseModifier {
  val name = "invalidjson"
}

/**
  * Generate response with invalid data.
  */
case object InvalidData extends ResponseModifier {
  val name = "invaliddata"
}

/**
  * Generates response as HTTP 204.
  */
case object NoBidNoContent extends ResponseModifier {
  val name = "nobidnocontent"
}

/**
  * Generates response with empty JSON.
  */
case object NoBidEmptyJson extends ResponseModifier {
  val name = "nobidemptyjson"
}

/**
  * Generates response with empty seat bids.
  */
case object NoBidEmptySeatBid extends ResponseModifier {
  val name = "nobidemptyseatbid"
}

/**
  * Generates response with valid win notice URL and ad markup in bid response.
  */
case object WinNoticeWithoutAdm extends ResponseModifier {
  val name = "winnotice-withoutadm"
}

/**
  * Generates response with valid win notice URL returning ad markup.
  */
case object WinNoticeWithAdm extends ResponseModifier {
  val name = "winnotice-withadm"
}

/**
  * Generates response with win notice URL returning 500 status code.
  */
case object BrokenWinNoticeWithAdm extends ResponseModifier {
  val name = "winnotice-broken"
}

/**
  * Generates response with win notice url returning ad markup after timeout.
  *
  * @param timeout timeout for win notice
  */
case class TimeoutWinNoticeWithAdm(timeout: Long) extends ResponseModifier {
  val name = s"${TimeoutWinNoticeWithAdm.prefix}$timeout"
}

/**
  * A companion object for [[com.bitworks.rtb.fake.bidder.TimeoutWinNoticeWithAdm TimeoutWinNoticeWithAdm]].
  */
object TimeoutWinNoticeWithAdm {
  val prefix = "winnotice-timeout-"
}

