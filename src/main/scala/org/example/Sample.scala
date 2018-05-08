package org.example

import com.job.analysis.CoreAnalyzer
import com.job.models.{Tweet, TweetSentiment}
import org.apache.spark.sql.SparkSession

object Sample {
  def main(args : Array[String])={
    import org.apache.spark.sql.Encoders
    val schema = Encoders.product[Tweet].schema

    val spark = SparkSession.builder.appName("Simple Application")
      .master("local[2]")
        .getOrCreate()

    import spark.implicits._
    val logData = spark
      .read
      .option("header", true)
      .schema(schema) //this is needed to solve upcasting issues
      .csv("/Users/tejashree.aher/Documents/rawTweets_CommaSeparated.csv").as[Tweet]

    val analysedDS = logData.map(tweet =>{
      println(s"________________-> ${Tweet.toString(tweet)}")
     TweetSentiment(tweet.tweet_id, tweet.text, tweet.createdOn, tweet.likes, CoreAnalyzer.extractSentiments(tweet.text))
    })
    analysedDS.write.csv(s"/tmp/analysedTweet")
    spark.stop()
  }
}
