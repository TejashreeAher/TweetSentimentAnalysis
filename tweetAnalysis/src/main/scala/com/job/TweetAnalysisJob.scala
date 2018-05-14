package com.job

import com.job.analysis.CoreAnalyzer
import com.job.config.{Configuration, InputConfig, OutpurConfig}
import com.job.functions.{AggregatedTweetSerialiser, TweetAggregatorFunction}
import com.job.models.{Tweet, TweetSentiment}
import org.apache.spark.sql.SparkSession
import utils.Utils

case class Args(date: String = "", keyWord: String = "#holidaycheck")

object TweetAnalysisJob {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.Encoders

    val JOB_NAME = "tweet-analysis-job"
//    val filePath = "/tmp/streaming/tweets"
//    val analyzer = new CoreAnalyzer()

    // parse the input date
    val parser = new scopt.OptionParser[Args](JOB_NAME) {
      head("date for which tweets are to be analysed")

      opt[String]('d', "date")
        .required()
        .text("Provide date for which tweets are to be analysed")
        .action { (x, c) => ///use -d or --date
          c.copy(date = x)
        }

      opt[String]('w', "keyword")
        .required()
        .text("keyword to be analysed")
        .action { (x, c) => ///use -d or --date
          c.copy(keyWord = x)
        }
    }

    val defaultParams = Args()
    val inputArgs = parser
      .parse(args, defaultParams)
      .getOrElse {
        parser.showUsageAsError
        sys.exit(1)
      }

    val conf = Configuration()
    val keyWord = inputArgs.keyWord.toLowerCase
    val inputConfig = pureconfig.loadConfigOrThrow[InputConfig](conf, "input")
    val outputConfig =
      pureconfig.loadConfigOrThrow[OutpurConfig](conf, "output")

    println(s"Received input path  : ${inputConfig.filePath}")
    println(
      s"Received  prefix : ${outputConfig.analysedPathPrefix}, aggregated prefix : ${outputConfig.aggregatedPathPrefix}")
    val schema = Encoders.product[Tweet].schema
    val executionDateRandomised =
      s"${inputArgs.date}-+${System.currentTimeMillis()}"

    val spark = SparkSession.builder
      .appName("Tweet Analyser")
      .master("local[2]")
      .getOrCreate()

    println(
      s"Reading from path ${inputConfig.filePath}/${keyWord}/${inputArgs.date}/*}")
    //for multiple files, as files will be as per date, read parallelly
    import spark.implicits._
    val logData = spark.read
      .option("header", false)
      .schema(schema) //this is needed to solve upcasting issues
      .csv(s"${inputConfig.filePath}/${keyWord}/${inputArgs.date}/*")
//      .csv("/Users/tejashree.aher/Documents/HC-input2/*")
      .as[Tweet]

    val analysedDS = logData
//      .distinct() //distinct shuffles data but makes sure duplicates in files are removed
      .map(tweet => {
        println(s"________________-> ${Tweet.toString(tweet)}")
        TweetSentiment(tweet.tweet_id,
                       tweet.text,
                       tweet.createdOn,
                       tweet.likes,
                       new CoreAnalyzer().extractSentiments(tweet.text).name)
      })
    analysedDS.write.json(
      s"${outputConfig.analysedPathPrefix}/${keyWord}/${inputArgs.date}/${executionDateRandomised}")

    analysedDS
      .map(x => Utils.getAggregatedTweet(x))
      .groupByKey(x => Utils.parseDate(x.date))
      .reduceGroups(TweetAggregatorFunction())
      .map(dayWiseTweet => AggregatedTweetSerialiser.serialise(dayWiseTweet))
      .coalesce(1) //doesn't do a full shuffle, combines partitions to create one partition, think about this again
      .write
      .json(
        s"${outputConfig.aggregatedPathPrefix}/${keyWord}/${inputArgs.date}/${executionDateRandomised}")

    spark.stop()
  }

}
