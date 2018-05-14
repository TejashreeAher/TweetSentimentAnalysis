package com.job.functions

import com.job.models.{DayWiseAggregatedTweet, SerialisedAggregatedTweet}

object AggregatedTweetSerialiser {
  def serialise(
      t: (String, DayWiseAggregatedTweet)): SerialisedAggregatedTweet = {
    SerialisedAggregatedTweet(t._1,
                              t._2.totalLikes,
                              t._2.totalPositives,
                              t._2.totalNegatives,
                              t._2.totalNeutrals)
  }
}
