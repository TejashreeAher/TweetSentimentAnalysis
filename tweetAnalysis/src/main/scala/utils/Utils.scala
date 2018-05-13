package utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

import com.job.models.{AggregatedTweet, Sentiment, TweetSentiment}

object Utils {
  def parseDate(date: String)={
    val twitterFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    // Important note. Only ENGLISH Locale works.
    val sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
    sf.setLenient(true);
    val parsedDate = sf.parse(date);
    LocalDate.of(parsedDate.getYear+1900, parsedDate.getMonth+1, parsedDate.getDate).toString
  }

   def getAggregatedTweet(x: TweetSentiment): AggregatedTweet = {
    val sentiment = x.sentiment
    val result = sentiment match {
      case Sentiment.POSITIVE => AggregatedTweet(x.tweet_id, x.likes.getOrElse(0), 1, 0 ,0, x.createdOn)
      case Sentiment.NEGATIVE => AggregatedTweet(x.tweet_id, x.likes.getOrElse(0), 0, 1 ,0, x.createdOn)
      case Sentiment.NEUTRAL =>  AggregatedTweet(x.tweet_id, x.likes.getOrElse(0), 0, 0 ,1, x.createdOn)
    }
    result
  }
}
