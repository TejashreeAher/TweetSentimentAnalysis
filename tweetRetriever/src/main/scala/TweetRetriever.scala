import java.io.FileInputStream
import java.util.{Date, Properties}

import twitter4j._
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConverters._

object TweetRetriever {

  val MAX_TWEETS = 100
  val MAX_LIMIT = 5

  case class Config(twitterConfigFilePath : String)

  def main(args: Array[String]):Unit ={
    //    // (1) config work to create a twitter object
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
//    twitter.addRateLimitStatusListener(new TwitterrateLimitListener())

    val searchRateLimit = twitter.getRateLimitStatus("search")
    val searchTweetsLimit = searchRateLimit.get("/search/tweets")

    var numTweetsSearched = 100
    var allTweets: List[Tweet] = List()
    var maxIdToSearch = -1L

    while(numTweetsSearched != 0){
      println(s"Remaining time -> ${searchTweetsLimit.getRemaining}")
      while(searchTweetsLimit.getRemaining == 0){
        println(s"******* API LIMIT REACHED, sleeping for time : ${searchTweetsLimit.getSecondsUntilReset + 100}")
        Thread.sleep((searchTweetsLimit.getSecondsUntilReset + 5)*1000)
      }
      //call twitter to get tweets
      val tweetsSearched = callTwitterSearchAPI(twitter, "#holidaycheck", maxIdToSearch, "2018-01-01")
      val lowestIdAndTweets = processAndGetNextMaxId(tweetsSearched.asScala.toList)
      allTweets = allTweets.++(lowestIdAndTweets._2)
      numTweetsSearched = lowestIdAndTweets._2.size
      maxIdToSearch = lowestIdAndTweets._1-1
    }

    println("Total number of tweets received : "+ allTweets.size)


  }

  def processAndGetNextMaxId(tweets: List[Status]) ={
    var lowestId = -1L ///this should actually the lowest id in this batch, which is the maxId for next request
    val processedTweets = tweets.map(tweet => {
      if(lowestId == -1 || tweet.getId < lowestId) lowestId = tweet.getId
      Tweet(tweet.getId, tweet.getText, tweet.getFavoriteCount, tweet.getCreatedAt)
    })
    (lowestId, processedTweets)
  }


  def callTwitterSearchAPI(twitter: Twitter, queryString: String, maxID: Long, timeSince: String)={
    // (2) use the twitter object to get your friend's timeline
    var query = new Query("eurovision")
    query.setSince("2018-01-01")
    query.setMaxId(maxID)
    query.setCount(MAX_TWEETS)

    println(s"Getting from date : ${query.getSince}")

    val queryResult = twitter.search(query)
    println(s"Result has  : ${queryResult.getTweets.size()} tweets")

    queryResult.getTweets
  }


case class Tweet(id: Long, text: String, likes: Int, createdOn: Date)

//  class TwitterrateLimitListener extends RateLimitStatusListener{
//    override def onRateLimitReached(event: RateLimitStatusEvent): Unit = {
//      println("your RATE limit reached, stop requesting")
//    }
//
//    override def onRateLimitStatus(event: RateLimitStatusEvent): Unit = {
//      println("Rate remaining is : "+ event.getRateLimitStatus)
//    }
//  }

}
