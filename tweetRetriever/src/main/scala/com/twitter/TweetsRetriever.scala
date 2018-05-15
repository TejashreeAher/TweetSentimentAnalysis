package com.twitter

import java.io.FileInputStream
import java.util.Properties

import com.twitter.hbc.core.endpoint.{StatusesFilterEndpoint, StreamingEndpoint}
import com.twitter.mapper.{HdfsEncoder, TextProcessor, Tweet, TweetMapper}
import com.twitter.source.HistoricTweetSource
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.fs.bucketing.{
  BucketingSink,
  DateTimeBucketer
}
import org.apache.flink.streaming.connectors.twitter.TwitterSource
import org.apache.flink.streaming.connectors.twitter.TwitterSource.EndpointInitializer

import scala.collection.JavaConverters._

object TweetsRetriever {
  val JOB_NAME = "twitter-streaming-job"
  val filePath = "/tmp/streaming/tweets/" //change this to take keyword from the argument

  case class Args(startDate: String = "",
                  keyWord: String = "#holidayCheck",
                  twitterPropsFile: String = "")

  def main(args: Array[String]): Unit = {
    // parse the input date
    val parser = new scopt.OptionParser[Args](JOB_NAME) {
      head("twitter streaming job")

      opt[String]('d', "start-date")
        .required()
        .text("Provide date ffrom which old tweets are to be retrieved")
        .action { (x, c) => ///use -d or --date
          c.copy(startDate = x)
        }

      opt[String]('w', "keyword")
        .required()
        .text("Enter the keyword to be filtered from Twitter")
        .action { (x, c) => ///use -w or --keyword
          c.copy(keyWord = x)
        }
      opt[String]('f', "file")
        .required()
        .text("Give the path to the file with Twitter credentials")
        .action { (x, c) => ///use -w or --keyword
          c.copy(twitterPropsFile = x)
        }
    }

    val defaultParams = Args()
    val inputArgs: Args = parser
      .parse(args, defaultParams)
      .getOrElse {
        parser.showUsageAsError
        sys.exit(1)
      }

    val bootstrapStartDate = inputArgs.startDate
    val keyWord = inputArgs.keyWord
    println(s"Keyword is : ${keyWord}")

    implicit val typeInfo = TypeInformation.of(classOf[(Tweet)])
    // get the execution environment
    val env: StreamExecutionEnvironment =
      StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(1000) //doesn't work in standalone mode. Works in production mode ...Restarting jobmanager will lose this checkpoint

    val twitterProps = new Properties()
    twitterProps.load(new FileInputStream(inputArgs.twitterPropsFile))
//    twitterProps.load(getClass.getResourceAsStream("/twitter.properties"))

    /***Code to listen to twitter stream***/
    val props = new Properties()
    props.setProperty(TwitterSource.CONSUMER_KEY,
                      twitterProps.get("CONSUMER_KEY").toString)
    props.setProperty(TwitterSource.CONSUMER_SECRET,
                      twitterProps.get("CONSUMER_SECRET").toString)
    props.setProperty(TwitterSource.TOKEN,
                      twitterProps.get("ACCESS_TOKEN").toString)
    props.setProperty(TwitterSource.TOKEN_SECRET,
                      twitterProps.get("ACCESS_TOKEN_SECRET").toString)

    val source = new TwitterSource(props)
    source.setCustomEndpointInitializer(new CustomEndpoint(keyWord))
    val liveStream = env
      .addSource(source)
      .flatMap(new TweetMapper())
      .flatMap(new TextProcessor())

    val bootStrapStream =
      env.addSource(new HistoricTweetSource(bootstrapStartDate, keyWord))

    val tweetStream = bootStrapStream
      .union(liveStream)

    // Output stream 2: write **newly** scraped articles to HDFS, as this is streaming, buckets will be as per tweets date too
    val rollingSink = new BucketingSink[String](s"${filePath}/${keyWord}")
    rollingSink
      .setBucketer(new DateTimeBucketer("yyyy-MM-dd"))

    tweetStream
      .map(new HdfsEncoder())
      .addSink(rollingSink)

    env.execute("Twitter-Streaming-Job")
  }

  class CustomEndpoint(keyWord: String)
      extends EndpointInitializer
      with Serializable {
    override def createEndpoint(): StreamingEndpoint = {
      val endpoint = new StatusesFilterEndpoint()
      println(s"creating endpoint for keyword : ${keyWord}")
      endpoint.trackTerms(List(keyWord).asJava)
//      endpoint.setBackfillCount(1000)
      endpoint
    }
  }

}
