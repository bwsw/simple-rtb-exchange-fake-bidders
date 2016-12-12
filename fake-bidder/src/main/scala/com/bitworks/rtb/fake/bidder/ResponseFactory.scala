package com.bitworks.rtb.fake.bidder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

/**
  * Creates BidResponse for BidRequest.
  *
  * @author Pavel Tomskikh
  */
class ResponseFactory {
  val mapper = new ObjectMapper

  /**
    * Returns ad markup for given imp type.
    *
    * @param impType  impression type
    * @param modifier bid response modifier
    */
  def getAdm(impType: String, modifier: Option[ResponseModifier]) = {
    modifier match {
      case Some(WinNoticeWithoutAdm) => ""
      case Some(WinNoticeWithAdm) | Some(TimeoutWinNoticeWithAdm(_)) =>
        val node = impType match {
          case "1" => bannerResponse
          case "2" => videoResponse
          case "3" => nativeResponse
        }
        node
          .get("seatbid")
          .elements
          .next
          .get("bid")
          .elements
          .next.get("adm")
          .textValue

      case _ => throw new RuntimeException
    }
  }

  /**
    * Returns Array[Byte] representation of BidResponse for BidRequest.
    *
    * @param inputBytes BidRequest
    * @return
    */
  def createBidResponse(
      inputBytes: Array[Byte],
      price: Option[BigDecimal],
      modifier: Option[ResponseModifier],
      winHost: Option[String]): Array[Byte] = {

    val bidRequest = mapper.readTree(inputBytes)

    val bidRequestId = bidRequest.get("id")
    if (bidRequestId == null || !bidRequestId.isTextual) throw new IllegalArgumentException

    val impArray = bidRequest.get("imp")
    if (impArray == null || !impArray.isArray || !impArray.elements.hasNext) throw new IllegalArgumentException

    val imp = impArray.elements.next
    val impId = imp.get("id")
    if (impId == null || !impId.isTextual) throw new IllegalArgumentException

    val impType = if ( imp.has("banner") ) 1
    else if ( imp.has("video") ) 2
    else if ( imp.has("native") ) 3
    else throw new IllegalArgumentException

    val bidResponse = impType match {
      case 1 => bannerResponse.deepCopy
      case 2 => videoResponse.deepCopy
      case 3 => nativeResponse.deepCopy
    }

    bidResponse.replace("id", bidRequestId)
    val bidNode = bidResponse
      .get("seatbid")
      .elements
      .next
      .asInstanceOf[ObjectNode]
      .get("bid")
      .elements
      .next
      .asInstanceOf[ObjectNode]

    bidNode.replace("impid", impId)
    bidNode.put("price", price.getOrElse(defaultPrice).bigDecimal)

    modifier match {
      case Some(InvalidJson) =>
        val correctBytes = mapper.writeValueAsBytes(bidResponse)
        return '}'.toByte +: correctBytes :+ ','.toByte

      case Some(NoBidNoContent) =>
        return new Array[Byte](0)

      case Some(InvalidData) =>
        bidNode.put("impid", s"incorrectImpId${impId.textValue}")

      case Some(NoBidEmptyJson) =>
        bidResponse.removeAll()

      case Some(NoBidEmptySeatBid) =>
        bidResponse.set("seatbid", mapper.createArrayNode)

      case Some(WinNoticeWithoutAdm) =>
        bidNode.put("nurl", s"http://${winHost.get}/win-notice?modifier=winnotice-withoutadm&type=$impType")

      case m@Some(WinNoticeWithAdm | TimeoutWinNoticeWithAdm(_) | BrokenWinNoticeWithAdm) =>
        bidNode.put("nurl", s"http://${winHost.get}/win-notice?modifier=${m.get.toString}&type=$impType")
        bidNode.remove("adm")

      case _ =>
    }
    mapper.writeValueAsBytes(bidResponse)
  }

  private def getJsonFromFile(fileName: String) = {
    val stream = getClass.getResourceAsStream(fileName)

    mapper.readTree(stream).asInstanceOf[ObjectNode]
  }

  private val defaultPrice = BigDecimal("50")
  private lazy val bannerResponse = getJsonFromFile("banner.json")
  private lazy val videoResponse = getJsonFromFile("video.json")
  private lazy val nativeResponse = getJsonFromFile("native.json")
}
