package com.job

import com.job.analysis.CoreAnalyzer
import com.job.functions.TweetAggregatorFunction
import com.job.models.{AggregatedTweet, Tweet, TweetSentiment}
import org.apache.spark.sql.SparkSession
import utils.Utils

import scala.util.Random

object TweetAnalysisJob {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.Encoders
    val schema = Encoders.product[Tweet].schema
    val rand = new Random()
    val startDate = "2018-05-10-"+{rand.nextInt(1000).toString}

    val spark = SparkSession.builder.appName("Tweet Analyser")
      .master("local[2]")
      .getOrCreate()

    //for multiple files, as files will be as per date, read parallelly
    import spark.implicits._
    val logData = spark
      .read
      .option("header", true)
      .schema(schema) //this is needed to solve upcasting issues
      .csv("/Users/tejashree.aher/Documents/rawTweets_CommaSeparated.csv")
      .as[Tweet]


    val analysedDS = logData.map(tweet =>{
      println(s"________________-> ${Tweet.toString(tweet)}")
      TweetSentiment(tweet.tweet_id, tweet.text, tweet.createdOn, tweet.likes, CoreAnalyzer.extractSentiments(tweet.text))
    })
    analysedDS.write.json(s"/tmp/analysedTweet-${startDate}")

    //groupby date and aggregate
    val aggregatedTweetRDD = analysedDS.map(x=>AggregatedTweet(x.tweet_id, x.likes.getOrElse(0), if(x.sentiment>2)1 else 0, if(x.sentiment<2)1 else 0, if(x.sentiment==2)1 else 0, x.createdOn))
      .groupByKey(x => Utils.getDatefromString(x.date))
      .reduceGroups(TweetAggregatorFunction())
        .coalesce(1) //doesn't do a full shuffle, combines partitions to create one partition, think about this again
        .write.json(s"/tmp/aggregatedTweet-${startDate}")

    spark.stop()
  }

}
