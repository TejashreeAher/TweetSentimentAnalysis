package com.job.analysis

import com.job.models.Sentiment

trait Analyzer {

  def extractSentiments(text: String): Sentiment

  def getSentiment(sentiments: List[(String, Int)]): Sentiment

  def toSentiment(sentiment: Int): Sentiment
}
