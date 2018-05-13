package com.job

import com.job.analysis.CoreAnalyzer
import com.job.functions.TweetAggregatorFunction
import com.job.models.{Tweet, TweetSentiment}
import org.apache.spark.sql.SparkSession
import utils.Utils

import scala.util.Random

object TweetAnalysisJob {
  def main(args: Array[String]): Unit = {
    val filePath = "/tmp/flink/tweets/"
    val executionDate = "2018-05-13"

    import org.apache.spark.sql.Encoders
    val schema = Encoders.product[Tweet].schema
    val rand = new Random()
    val executionDateRandomised = "2018-05-13-"+{rand.nextInt(1000).toString}

    val spark = SparkSession.builder.appName("Tweet Analyser")
      .master("local[2]")
      .getOrCreate()

    println(s"Reading from path /tmp/flink/tweets/${executionDate}/*}")
    //for multiple files, as files will be as per date, read parallelly
    import spark.implicits._
    val logData = spark
      .read
      .option("header", false)
      .schema(schema) //this is needed to solve upcasting issues
        .csv(s"/tmp/flink/tweets/${executionDate}/*")
//      .csv("/Users/tejashree.aher/Documents/HC-input2/*")
      .as[Tweet]


    val analysedDS = logData.map(tweet =>{
      println(s"________________-> ${Tweet.toString(tweet)}")
      TweetSentiment(tweet.tweet_id, tweet.text, tweet.createdOn, tweet.likes, CoreAnalyzer.extractSentiments(tweet.text))
    })
    analysedDS.write.json(s"/tmp/analysedTweet-${executionDateRandomised}")

    analysedDS.map(x => Utils.getAggregatedTweet(x))
      .groupByKey(x => Utils.parseDate(x.date))
      .reduceGroups(TweetAggregatorFunction())
      .coalesce(1) //doesn't do a full shuffle, combines partitions to create one partition, think about this again
      .write.json(s"/tmp/aggregatedTweet-${executionDate}")

    spark.stop()
  }

}
