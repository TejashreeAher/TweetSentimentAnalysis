package com.twitter.functions

import java.io.FileInputStream
import java.util.Properties

import com.twitter.mapper.Tweet
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Query, Status, Twitter, TwitterFactory}

import scala.collection.JavaConverters._

class HistoricTweetRetriever(startDate : String, keyWord : String) {
  val MAX_TWEETS = 100
  val MAX_LIMIT = 5

  case class Config(twitterConfigFilePath : String)

  def getConfigBuilder() = {
    val authProps =new Properties()
    authProps.load(new FileInputStream(getClass.getResource("/twitter.properties").getPath))

    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(authProps.getProperty("CONSUMER_KEY"))
      .setOAuthConsumerSecret(authProps.getProperty("CONSUMER_SECRET"))
      .setOAuthAccessToken(authProps.getProperty("ACCESS_TOKEN"))
      .setOAuthAccessTokenSecret(authProps.getProperty("ACCESS_TOKEN_SECRET"))
    cb
  }

  def getTweetsForKeyWork() ={
    val cb = getConfigBuilder()

    val tf = new TwitterFactory(cb.build)
    val twitter = tf.getInstance()
    //    twitter.addRateLimitStatusListener(new TwitterrateLimitListener())

    val searchRateLimit = twitter.getRateLimitStatus("search")
    val searchTweetsLimit = searchRateLimit.get("/search/tweets")
    var remainingLimit = searchTweetsLimit.getRemaining

    var numTweetsSearched = 100
    var allTweets: List[Tweet] = List()
    var maxIdToSearch = -1L

    while(numTweetsSearched != 0){
      println(s"BOOTSTRAP : Remaining time -> ${remainingLimit}")
      if(remainingLimit == 0) {
        while (twitter.getRateLimitStatus("search").get("/search/tweets").getRemaining == 0) {
          println(s"******* API LIMIT REACHED, sleeping for time : ${searchTweetsLimit.getSecondsUntilReset}")
          Thread.sleep((searchTweetsLimit.getSecondsUntilReset + 5) * 1000)
        }
        remainingLimit = twitter.getRateLimitStatus("search").get("/search/tweets").getRemaining
      }
      //call twitter to get tweets
      val tweetsSearched = callTwitterSearchAPI(twitter, keyWord, maxIdToSearch, startDate)
      remainingLimit = remainingLimit-1
      val lowestIdAndTweets = processAndGetNextMaxId(tweetsSearched.asScala.toList)
      allTweets = allTweets.++(lowestIdAndTweets._2)
      numTweetsSearched = lowestIdAndTweets._2.size
      maxIdToSearch = lowestIdAndTweets._1-1
    }

    println("Total number of tweets received : "+ allTweets.size)
    allTweets

  }

  def processAndGetNextMaxId(tweets: List[Status]) ={
    var lowestId = -1L ///this should actually the lowest id in this batch, which is the maxId for next request
    val processedTweets = tweets.map(tweet => {
      if(lowestId == -1 || tweet.getId < lowestId) lowestId = tweet.getId
      Tweet(tweet.getId, tweet.getCreatedAt.toString, tweet.getText, tweet.getFavoriteCount)
    })
    (lowestId, processedTweets)
  }


  def callTwitterSearchAPI(twitter: Twitter, queryString: String, maxID: Long, timeSince: String)={
    // (2) use the twitter object to get your friend's timeline
    println(s"Max id is : ${maxID}")
    var query = new Query(queryString)
    query.setSince(startDate)
    query.setMaxId(maxID)
    query.setCount(MAX_TWEETS)

    val queryResult = twitter.search(query)
    println(s"Result has  : ${queryResult.getTweets.size()} tweets")

    queryResult.getTweets
  }

}

