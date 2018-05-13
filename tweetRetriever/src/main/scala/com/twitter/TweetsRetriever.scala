package com.twitter

//this is needed to fix type errors while consuming the datastream
import java.util.Properties

import com.twitter.hbc.core.endpoint.{StatusesFilterEndpoint, StreamingEndpoint}
import com.twitter.mapper.TweetMapper
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.twitter.TwitterSource
import org.apache.flink.streaming.connectors.twitter.TwitterSource.EndpointInitializer
import org.apache.flink.streaming.api.scala._

import scala.collection.JavaConverters._


object TweetsRetriever {
  val JOB_NAME = "twitter-stream"
  case class Config(startDate: String= "", endDate : String = "")

  def main(args: Array[String]): Unit = {
    // get the execution environment
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(1000) //doesn't work in standalone mode. Works in production mode ...Restarting jobmanager will lose this checkpoint

    val twitterProps = new Properties()
    twitterProps.load(getClass.getResourceAsStream("/twitter.properties"))

    /***Code to listen to twitter stream***/
    val props = new Properties()
    props.setProperty(TwitterSource.CONSUMER_KEY, twitterProps.get("CONSUMER_KEY").toString)
    props.setProperty(TwitterSource.CONSUMER_SECRET, twitterProps.get("CONSUMER_SECRET").toString)
    props.setProperty(TwitterSource.TOKEN, twitterProps.get("ACCESS_TOKEN").toString)
    props.setProperty(TwitterSource.TOKEN_SECRET, twitterProps.get("ACCESS_TOKEN_SECRET").toString)
    val source = new TwitterSource(props)
    source.setCustomEndpointInitializer(new CustomEndpoint())

    val streamSource = env.addSource(source)

    streamSource.flatMap(new TweetMapper())
        .print()

    env.execute("Twitter streaming")
  }

  class CustomEndpoint extends EndpointInitializer with Serializable
  {
    override def createEndpoint(): StreamingEndpoint = {
      val endpoint = new StatusesFilterEndpoint()
      endpoint.trackTerms(List("dance", "#", "It", "it", "a", "#ESF2018", "#esf18", "dutch", "Dutch", "Amsterdam", "amsterdam", "#Eurovision").asJava)
      endpoint
    }
  }

}
