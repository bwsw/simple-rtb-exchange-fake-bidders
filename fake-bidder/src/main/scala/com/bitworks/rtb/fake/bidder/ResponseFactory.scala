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
    * Returns Array[Byte] representation of BidResponse for BidRequest.
    *
    * @param inputBytes BidRequest
    * @return
    */
  def createBidResponse(
      inputBytes: Array[Byte],
      price: Option[BigDecimal],
      modifier: Option[ResponseModifier]): Array[Byte] = {

    val bidRequest = mapper.readTree(inputBytes)

    val bidRequestId = bidRequest.get("id")
    if (bidRequestId == null || !bidRequestId.isTextual) throw new IllegalArgumentException

    val impArray = bidRequest.get("imp")
    if (impArray == null || !impArray.isArray || !impArray.elements.hasNext) throw new IllegalArgumentException

    val imp = impArray.elements.next
    val impId = imp.get("id")
    if (impId == null || !impId.isTextual) throw new IllegalArgumentException

    var bidResponse: ObjectNode = null
    if (imp.has("banner")) bidResponse = bannerResponse.deepCopy
    else if (imp.has("video")) bidResponse = videoResponse.deepCopy
    else if (imp.has("native")) bidResponse = nativeResponse.deepCopy
    else throw new IllegalArgumentException

    bidResponse.replace("id", bidRequestId)
    bidResponse
      .get("seatbid")
      .elements
      .next
      .asInstanceOf[ObjectNode]
      .get("bid")
      .elements
      .next
      .asInstanceOf[ObjectNode]
      .replace("impid", impId)

    val resultingPrice = price.getOrElse(defaultPrice).bigDecimal
    bidResponse
      .get("seatbid")
      .elements
      .next
      .asInstanceOf[ObjectNode]
      .get("bid")
      .elements
      .next
      .asInstanceOf[ObjectNode]
      .put("price", resultingPrice)

    modifier match {
      case Some(InvalidJson) =>
        val correctBytes = mapper.writeValueAsBytes(bidResponse)
        '}'.toByte +: correctBytes :+ ','.toByte
      case Some(InvalidData) =>
        bidResponse
          .get("seatbid")
          .elements
          .next
          .asInstanceOf[ObjectNode]
          .get("bid")
          .elements
          .next
          .asInstanceOf[ObjectNode]
          .put("impid", s"incorrectImpId${impId.textValue}")
        mapper.writeValueAsBytes(bidResponse)
      case Some(NoBidNoContent) =>
        new Array[Byte](0)
      case Some(NoBidEmptyJson) =>
        mapper.writeValueAsBytes(mapper.createObjectNode)
      case Some(NoBidEmptySeatBid) =>
        bidResponse.set("seatbid", mapper.createArrayNode)
        mapper.writeValueAsBytes(bidResponse)
      case None =>
        mapper.writeValueAsBytes(bidResponse)
    }
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
