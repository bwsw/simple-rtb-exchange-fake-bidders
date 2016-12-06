package com.bitworks.rtb.fake.bidder

/**
  * Returns response modifier model.
  *
  * @author Egor Ilchenko
  */
class ResponseModifierFactory {

  /**
    * Returns bid response modifier.
    *
    * @param modifierStr some modifier from query string
    */
  def getModifier(modifierStr: Option[String]) = {
    modifierStr match {
      case Some(str) =>
        Some(str match {
          case "invalidjson" => InvalidJson
          case "invaliddata" => InvalidData
          case "nobidnocontent" => NoBidNoContent
          case "nobidemptyjson" => NoBidEmptyJson
          case "nobidemptyseatbid" => NoBidEmptySeatBid
          case "winnotice-withoutadm" => WinNoticeWithoutAdm
          case "winnotice-withadm" => WinNoticeWithAdm
          case "winnotice-broken" => BrokenWinNoticeWithAdm
          case s if s.startsWith("winnotice-timeout-") =>
            val timeout = s.substring(18).toLong
            TimeoutWinNoticeWithAdm(timeout)
          case _ => throw new IllegalArgumentException(s"$str is not valid modifier")
        })
      case None => None
    }
  }
}
