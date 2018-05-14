package com.job.models

case class Sentiment(value: Int, name: String)

object Sentiment {
  val POSITIVE_SENTIMENT = "positive"
  val NEGATIVE_SENTIMENT = "negative"
  val NEUTRAL_SENTIMENT = "neutral"
}
