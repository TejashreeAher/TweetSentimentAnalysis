package com.twitter

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
  val JOB_NAME = "twitter-stream"
  val bootstrapStartDate = "2018-01-01"
  val keyWord = "#holidaycheck"
  val filePath = "/tmp/streaming/tweets/"

  case class Config(startDate: String = "", endDate: String = "")

  def main(args: Array[String]): Unit = {

    implicit val typeInfo = TypeInformation.of(classOf[(Tweet)])
    // get the execution environment
    val env: StreamExecutionEnvironment =
      StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(1000) //doesn't work in standalone mode. Works in production mode ...Restarting jobmanager will lose this checkpoint

    val twitterProps = new Properties()
    twitterProps.load(getClass.getResourceAsStream("/twitter.properties"))

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
    source.setCustomEndpointInitializer(new CustomEndpoint())
    val liveStream = env
      .addSource(source)
      .flatMap(new TweetMapper())
      .flatMap(new TextProcessor())

    val bootStrapStream =
      env.addSource(new HistoricTweetSource(bootstrapStartDate, keyWord))

    val tweetStream = bootStrapStream.union(liveStream)

    // Output stream 2: write **newly** scraped articles to HDFS, as this is streaming, buckets will be as per tweets date too
    val rollingSink = new BucketingSink[String](filePath)
    rollingSink.setBucketer(new DateTimeBucketer("yyyy-MM-dd")) //this is default

    tweetStream
      .map(new HdfsEncoder())
      .addSink(rollingSink)

    env.execute("Twitter-Streaming-Job")
  }

  class CustomEndpoint extends EndpointInitializer with Serializable {
    override def createEndpoint(): StreamingEndpoint = {
      val endpoint = new StatusesFilterEndpoint()
      endpoint.trackTerms(List(keyWord).asJava)
      endpoint
    }
  }

}
