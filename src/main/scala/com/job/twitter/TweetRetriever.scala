package com.job.twitter

import java.io.FileInputStream

import twitter4j.{Query, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder
import java.util.Properties
import scala.collection.JavaConverters._

//check if tweets need to be filtered by language
case class TweetRetriever(startDate : Long,
                          endDate : Long,
                          searchKey: String)

object TweetRetriever {
  def main(args: Array[String]):Unit ={
    // (1) config work to create a twitter object
    val authProps =new Properties()
    authProps.load(new FileInputStream(getClass.getResource("/twitter.properties").getPath))

    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(authProps.getProperty("CONSUMER_KEY"))
      .setOAuthConsumerSecret(authProps.getProperty("CONSUMER_SECRET"))
      .setOAuthAccessToken(authProps.getProperty("ACCESS_TOKEN"))
      .setOAuthAccessTokenSecret(authProps.getProperty("ACCESS_TOKEN_SECRET"))

    val tf = new TwitterFactory(cb.build)
    val twitter = tf.getInstance()

    // (2) use the twitter object to get your friend's timeline
    var query = new Query("#holidaycheck")
    query.setSince("2018-01-01")
    query.setCount(100)

    println(s"Getting from date : ${query.getSince}")

    val queryResult = twitter.search(query)
    println(s"Result has  : ${queryResult.getTweets.size()} tweets")

     queryResult.getTweets.asScala.map{x => {
        println(s"TWEET : ${x.getText} BY ${x.getUser} ON ${x.getCreatedAt}")}
      }

  }

}
