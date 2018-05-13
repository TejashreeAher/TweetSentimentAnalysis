package com.twitter.source

import java.io.FileInputStream
import java.util.Properties

import com.twitter.mapper.Tweet
import org.apache.flink.api.common.functions.StoppableFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Query, Status, Twitter, TwitterFactory}

import scala.collection.JavaConverters._


class HistoricTweetSource(startDate: String, keyWord: String) extends SourceFunction[Tweet] with StoppableFunction {
  val MAX_TWEETS = 100
  val MAX_LIMIT = 5


  var isRunning = true

  override def cancel(): Unit = {
  }

  override def run(ctx: SourceFunction.SourceContext[Tweet]): Unit = {
    val cb = getConfigBuilder()

    val tf = new TwitterFactory(cb.build)

    val twitter = tf.getInstance()
//  val tweets = new HistoricTweetRetriever(startDate, keyWord).getTweetsForKeyWork()
    val searchRateLimit = twitter.getRateLimitStatus("search")
    val searchTweetsLimit = searchRateLimit.get("/search/tweets")
    var remainingLimit = searchTweetsLimit.getRemaining

    var numTweetsSearched = 100
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
      val tweetsTillNow = lowestIdAndTweets._2
      numTweetsSearched = lowestIdAndTweets._2.size
      maxIdToSearch = lowestIdAndTweets._1-1

      tweetsTillNow.map(x => ctx.collect(x))
    }
    stop()
//    isRunning = false

  }

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

//  def getTweetsForKeyWork() ={
//    val searchRateLimit = twitter.getRateLimitStatus("search")
//    val searchTweetsLimit = searchRateLimit.get("/search/tweets")
//    var remainingLimit = searchTweetsLimit.getRemaining
//
//    var numTweetsSearched = 100
//    var allTweets: List[Tweet] = List()
//    var maxIdToSearch = -1L
//
//    while(numTweetsSearched != 0){
//      println(s"BOOTSTRAP : Remaining time -> ${remainingLimit}")
//      if(remainingLimit == 0) {
//        while (twitter.getRateLimitStatus("search").get("/search/tweets").getRemaining == 0) {
//          println(s"******* API LIMIT REACHED, sleeping for time : ${searchTweetsLimit.getSecondsUntilReset}")
//          Thread.sleep((searchTweetsLimit.getSecondsUntilReset + 5) * 1000)
//        }
//        remainingLimit = twitter.getRateLimitStatus("search").get("/search/tweets").getRemaining
//      }
//      //call twitter to get tweets
//      val tweetsSearched = callTwitterSearchAPI(twitter, keyWord, maxIdToSearch, startDate)
//      remainingLimit = remainingLimit-1
//      val lowestIdAndTweets = processAndGetNextMaxId(tweetsSearched.asScala.toList)
//      allTweets = allTweets.++(lowestIdAndTweets._2)
//      numTweetsSearched = lowestIdAndTweets._2.size
//      maxIdToSearch = lowestIdAndTweets._1-1
//    }
//
//    println("Total number of tweets received : "+ allTweets.size)
//    allTweets
//
//  }

  /**
    * Maps the response into @Tweet objects and gets the lowest tweetid from the input tweet
    *
    * @param tweets - The list of tweets returned as response from Twitter API
    * @return - A tuple of lowest tweet id in the batch and the tweets
    */
  def processAndGetNextMaxId(tweets: List[Status]) ={
    var lowestId = -1L ///this should actually the lowest id in this batch, which is the maxId for next request
    val processedTweets = tweets.map(tweet => {
      if(lowestId == -1 || tweet.getId < lowestId) lowestId = tweet.getId
      Tweet(tweet.getId, tweet.getCreatedAt.toString, tweet.getText, tweet.getFavoriteCount)
    })
    (lowestId, processedTweets)
  }

  /**
    * Calls Twitter API
    *
    * @param twitter - Twitter object to call API
    * @param queryString - keyword to search for in Twitter API
    * @param maxID - the max tweet id to be searched
    * @param timeSince - time after which tweets are to be searched
    * @return - tweets returned by the API
    */
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

  override def stop(): Unit = {
    isRunning = false
  }
}

