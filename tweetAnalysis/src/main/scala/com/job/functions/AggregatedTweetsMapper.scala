package com.job.functions

import com.job.models.{DayWiseAggregatedTweet, Sentiment, TweetSentiment}
import org.apache.spark.api.java.function.MapFunction

class AggregatedTweetsMapper
    extends MapFunction[TweetSentiment, DayWiseAggregatedTweet] {
  override def call(x: TweetSentiment): DayWiseAggregatedTweet = {
    val sentiment = x.sentimentName
    val result = sentiment match {
      case Sentiment.POSITIVE_SENTIMENT =>
        DayWiseAggregatedTweet(x.likes.getOrElse(0), 1, 0, 0, x.createdOn)
      case Sentiment.NEGATIVE_SENTIMENT =>
        DayWiseAggregatedTweet(x.likes.getOrElse(0), 0, 1, 0, x.createdOn)
      case Sentiment.NEUTRAL_SENTIMENT =>
        DayWiseAggregatedTweet(x.likes.getOrElse(0), 0, 0, 1, x.createdOn)
    }
    result
  }
}
