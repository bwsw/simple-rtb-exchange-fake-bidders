package com.bitworks.rtb.fake.bidder

/**
  * Modifier for bid response.
  *
  * @author Egor Ilchenko
  */
trait ResponseModifier

/**
  * Generate response with invalid JSON syntax.
  */
case object InvalidJson extends ResponseModifier

/**
  * Generate response with invalid data.
  */
case object InvalidData extends ResponseModifier

/**
  * Generates response as HTTP 204.
  */
case object NoBidNoContent extends ResponseModifier

/**
  * Generates response with empty JSON.
  */
case object NoBidEmptyJson extends ResponseModifier

/**
  * Generates response with empty seat bids.
  */
case object NoBidEmptySeatBid extends ResponseModifier

/**
  * Generates response with valid win notice URL and ad markup in bid response.
  */
case object WinNoticeWithoutAdm extends ResponseModifier {
  override def toString = "winnotice-withoutadm"
}

/**
  * Generates response with valid win notice URL returning ad markup.
  */
case object WinNoticeWithAdm extends ResponseModifier {
  override def toString = "winnotice-withadm"
}

/**
  * Generates response with win notice URL returning 500 status code.
  */
case object BrokenWinNoticeWithAdm extends ResponseModifier {
  override def toString = "winnotice-broken"
}

/**
  * Generates response with win notice url returning ad markup after timeout.
  *
  * @param timeout timeout for win notice
  */
case class TimeoutWinNoticeWithAdm(timeout: Long) extends ResponseModifier {
  override def toString = s"winnotice-timeout-$timeout"
}
