package com.job.functions

import com.job.models.DayWiseAggregatedTweet
import org.apache.spark.api.java.function.ReduceFunction

case class TweetAggregatorFunction()
    extends ReduceFunction[DayWiseAggregatedTweet] {
  override def call(v1: DayWiseAggregatedTweet,
                    v2: DayWiseAggregatedTweet): DayWiseAggregatedTweet = {
    DayWiseAggregatedTweet(v1.totalLikes + v2.totalLikes,
                           v1.totalPositives + v2.totalPositives,
                           v1.totalNegatives + v1.totalNegatives,
                           v1.totalNeutrals + v2.totalNeutrals,
                           v1.date)
  }
}
