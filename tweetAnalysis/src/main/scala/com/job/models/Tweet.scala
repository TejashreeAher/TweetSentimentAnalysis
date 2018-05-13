package com.job.models

import com.job.models.Sentiment.Sentiment

//likes is Option[Int] because otherwise 0 likes give non-nullable error
case class Tweet(tweet_id: String, createdOn: String, text: String, likes: Option[Int])
case class TweetSentiment(tweet_id: String, text: String, createdOn: String, likes: Option[Int], sentiment : Sentiment)
case class AggregatedTweet(tweet_id: String, totalLikes: Int, totalPositives: Int, totalNegatives: Int, totalNeutrals: Int, date: String)

object Tweet{
  def toString(tweet : Tweet)={
    s"Tweet id : ${tweet.tweet_id}, created_on: ${tweet.createdOn}, text: ${tweet.text}, likes: ${tweet.likes}"
  }
}