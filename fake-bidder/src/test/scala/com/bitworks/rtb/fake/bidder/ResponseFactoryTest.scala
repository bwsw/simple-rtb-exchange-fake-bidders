package com.bitworks.rtb.fake.bidder

import java.io.File

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

    val response = factory.createBidResponse(request, None, None, None)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "create BidResponse for video request" in {
    val path = getClass.getResource("video_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val requestPath = getClass.getResource("video_request.json").getPath
    val request = Source.fromFile(requestPath).mkString.getBytes()

    val response = factory.createBidResponse(request, None, None, None)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "create BidResponse for native request" in {
    val path = getClass.getResource("native_response.json").getPath
    val file = new File(path)
    val expectedResponse = mapper.readTree(file)

    val requestPath = getClass.getResource("native_request.json").getPath
    val request = Source.fromFile(requestPath).mkString.getBytes()

    val response = factory.createBidResponse(request, None, None, None)
    val responseJson = mapper.readTree(response)

    responseJson shouldBe expectedResponse
  }

  it should "throw exception when it got request with empty imp array" in {
    val request = """{"id":"817568131","at":1,"cur":["USD"],"imp":{}}""".getBytes

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request, None, None, None))
  }

  it should "throw exception when it got request without id" in {
    val request = """{"at":1,"cur":["USD"],"imp":{}}""".getBytes

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request, None, None, None))
  }

  it should "throw exception when it got request without imp array" in {
    val request = """{"id":"1","at":1,"cur":["USD"]}""".getBytes

    an[IllegalArgumentException] shouldBe thrownBy(
      factory.createBidResponse(request, None, None, None))
  }

  it should "use non default price if it's necessary" in {
    Seq("banner", "video", "native").foreach { x =>
      val expectedPrice = BigDecimal(42.42)

      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, Some(expectedPrice), None, None)
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

  it should "return invalid JSON if modifier specified" in {
    Seq("banner", "video", "native").foreach { x =>
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(InvalidJson), None)
      an[Throwable] should be thrownBy {
        val responseJson = mapper.readTree(response)
      }
    }
  }

  it should "return JSON with invalid data if modifier specified" in {
    Seq("banner", "video", "native").foreach { x =>
      val expectedId = "1"

      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(InvalidData), None)
      val responseJson = mapper.readTree(response)

      val impId = responseJson
        .get("seatbid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("bid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("impid")
        .textValue()

      impId should not be expectedId
    }
  }

  it should "return empty JSON if modifier specified" in {
    Seq("banner", "video", "native").foreach { x =>
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(NoBidEmptyJson), None)
      val responseJson = mapper.readTree(response).toString

      responseJson shouldBe "{}"
    }
  }

  it should "return empty response if modifier specified" in {
    Seq("banner", "video", "native").foreach { x =>
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(NoBidNoContent), None)

      response shouldBe empty
    }
  }

  it should "return JSON with empty seat bid if modifier specified" in {
    Seq("banner", "video", "native").foreach { x =>
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(NoBidEmptySeatBid), None)
      val responseJson = mapper.readTree(response)

      an[NoSuchElementException] should be thrownBy {
        responseJson
          .get("seatbid")
          .elements
          .next
      }
    }
  }

  it should "use correct nurl for win notice without adm" in {
    Seq("banner" -> 1, "video" -> 2, "native" -> 3).foreach { case (x, n) =>
      val host = "host"
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(WinNoticeWithoutAdm), Some(host))
      val responseJson = mapper.readTree(response)

      val nurl = responseJson
        .get("seatbid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("bid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("nurl")
        .textValue()

      val expectedNurl = s"http://$host/win-notice?modifier=winnotice-withoutadm&type=$n"
      nurl shouldBe expectedNurl
    }
  }

  it should "use correct nurl and remove ad markup for win notice with adm" in {
    Seq("banner" -> 1, "video" -> 2, "native" -> 3).foreach { case (x, n) =>
      val host = "host"
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(WinNoticeWithAdm), Some(host))
      val responseJson = mapper.readTree(response)

      val bidNode = responseJson
        .get("seatbid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("bid")
        .elements
        .next
        .asInstanceOf[ObjectNode]

      val adm = bidNode
        .get("adm")

      val nurl = bidNode
        .get("nurl")
        .textValue()

      val expectedNurl = s"http://$host/win-notice?modifier=winnotice-withadm&type=$n"
      nurl shouldBe expectedNurl
      adm shouldBe null
    }
  }

  it should "use correct nurl and remove ad markup for win notice with timeout" in {
    Seq("banner" -> 1, "video" -> 2, "native" -> 3).foreach { case (x, n) =>
      val host = "host"
      val timeout = 1442
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(
        request,
        None,
        Some(TimeoutWinNoticeWithAdm(timeout)),
        Some(host))

      val responseJson = mapper.readTree(response)

      val bidNode = responseJson
        .get("seatbid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("bid")
        .elements
        .next
        .asInstanceOf[ObjectNode]

      val adm = bidNode
        .get("adm")

      val nurl = bidNode
        .get("nurl")
        .textValue()

      val expectedNurl = s"http://$host/win-notice?modifier=winnotice-timeout-$timeout&type=$n"
      nurl shouldBe expectedNurl
      adm shouldBe null
    }
  }

  it should "use correct nurl and remove ad markup for broken win notice" in {
    Seq("banner" -> 1, "video" -> 2, "native" -> 3).foreach { case (x, n) =>
      val host = "host"
      val requestPath = getClass.getResource(s"${x}_request.json").getPath
      val request = Source.fromFile(requestPath).mkString.getBytes()

      val response = factory.createBidResponse(request, None, Some(BrokenWinNoticeWithAdm), Some(host))
      val responseJson = mapper.readTree(response)

      val bidNode = responseJson
        .get("seatbid")
        .elements
        .next
        .asInstanceOf[ObjectNode]
        .get("bid")
        .elements
        .next
        .asInstanceOf[ObjectNode]

      val adm = bidNode
        .get("adm")

      val nurl = bidNode
        .get("nurl")
        .textValue()

      val expectedNurl = s"http://$host/win-notice?modifier=winnotice-broken&type=$n"
      nurl shouldBe expectedNurl
      adm shouldBe null
    }
  }
}
