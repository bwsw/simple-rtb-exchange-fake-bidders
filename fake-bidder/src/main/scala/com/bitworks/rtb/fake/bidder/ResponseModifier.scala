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
