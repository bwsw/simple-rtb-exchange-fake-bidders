package com.bitworks.rtb.fake_bidder

import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest.{FlatSpec, Matchers}

/**
  * Test for [[com.bitworks.rtb.fake_bidder.ResponseFactory ResponseFactory]].
  *
  * @author Pavel Tomskikh
  */
class ResponseFactoryTest extends FlatSpec with Matchers {

  val mapper = new ObjectMapper
  val factory = new ResponseFactory

  "ResponseFactory" should "create BidResponse for correctly banner request" in {
    val path = getClass.getResource("banner_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val request = getClass.getResource("banner_request.json").openStream
    val response = factory.createBidResponse(request)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "create BidResponse for correctly video request" in {
    val path = getClass.getResource("video_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val request = getClass.getResource("video_request.json").openStream
    val response = factory.createBidResponse(request)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "create BidResponse for correctly native request" in {
    val path = getClass.getResource("native_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val request = getClass.getResource("native_request.json").openStream
    val response = factory.createBidResponse(request)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "throw exception when it got incorrect request" in {
    val request = getClass.getResource("incorrect_request.json").openStream

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request))
  }
}
