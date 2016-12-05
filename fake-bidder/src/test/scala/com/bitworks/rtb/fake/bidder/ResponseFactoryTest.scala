package com.bitworks.rtb.fake.bidder

import java.io.{ByteArrayInputStream, File}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

/**
  * Test for [[com.bitworks.rtb.fake.bidder.ResponseFactory ResponseFactory]].
  *
  * @author Pavel Tomskikh
  */
class ResponseFactoryTest extends FlatSpec with Matchers {

  val mapper = new ObjectMapper
  val factory = new ResponseFactory

  "ResponseFactory" should "create BidResponse for banner request" in {
    val path = getClass.getResource("banner_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val requestPath = getClass.getResource("banner_request.json").getPath
    val request = Source.fromFile(requestPath).mkString.getBytes()

    val response = factory.createBidResponse(request, None)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "create BidResponse for video request" in {
    val path = getClass.getResource("video_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val requestPath = getClass.getResource("video_request.json").getPath
    val request = Source.fromFile(requestPath).mkString.getBytes()

    val response = factory.createBidResponse(request, None)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "create BidResponse for native request" in {
    val path = getClass.getResource("native_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val requestPath = getClass.getResource("native_request.json").getPath
    val request = Source.fromFile(requestPath).mkString.getBytes()

    val response = factory.createBidResponse(request, None)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "throw exception when it got request with empty imp array" in {
    val request = """{"id":"817568131","at":1,"cur":["USD"],"imp":{}}""".getBytes

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request, None))
  }

  it should "throw exception when it got request without id" in {
    val request = """{"at":1,"cur":["USD"],"imp":{}}""".getBytes

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request, None))
  }

  it should "throw exception when it got request without imp array" in {
    val request = """{"id":"1","at":1,"cur":["USD"]}""".getBytes

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request, None))
  }

  it should "use non default price if it's necessary" in {
    Seq("banner", "video", "native").foreach { x =>
      val expectedPrice = BigDecimal(42.42)

      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, Some(expectedPrice))
      val responseJson = mapper.readTree(response)

      val price = responseJson
        .get("seatbid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("bid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("price")
        .decimalValue()

      price shouldBe expectedPrice.bigDecimal
    }
  }
}
