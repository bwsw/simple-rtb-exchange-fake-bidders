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
        Some(
          str match {
            case InvalidJson.name => InvalidJson
            case InvalidData.name => InvalidData
            case NoBidNoContent.name => NoBidNoContent
            case NoBidEmptyJson.name => NoBidEmptyJson
            case NoBidEmptySeatBid.name => NoBidEmptySeatBid
            case WinNoticeWithoutAdm.name => WinNoticeWithoutAdm
            case WinNoticeWithAdm.name => WinNoticeWithAdm
            case BrokenWinNoticeWithAdm.name => BrokenWinNoticeWithAdm
            case s if s.startsWith(TimeoutWinNoticeWithAdm.prefix) =>
              val timeout = s.substring(TimeoutWinNoticeWithAdm.prefix.length).toLong
              TimeoutWinNoticeWithAdm(timeout)
            case _ => throw new IllegalArgumentException(s"$str is not valid modifier")
          })
      case None => None
    }
  }
}
