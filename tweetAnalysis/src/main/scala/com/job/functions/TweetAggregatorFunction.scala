package com.job.functions

import com.job.models.AggregatedTweet
import org.apache.spark.api.java.function.ReduceFunction


case class TweetAggregatorFunction() extends ReduceFunction[AggregatedTweet]{
  override def call(v1: AggregatedTweet, v2: AggregatedTweet): AggregatedTweet = {
    AggregatedTweet(v1.tweet_id, v1.totalLikes+v2.totalLikes, v1.totalPositives+v2.totalPositives, v1.totalNegatives+v1.totalNegatives, v1.totalNeutrals+v2.totalNeutrals, v1.date)
  }
}
