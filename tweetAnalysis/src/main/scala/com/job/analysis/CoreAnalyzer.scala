package com.job.analysis

import java.util.Properties

import com.job.models.Sentiment
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

import scala.collection.JavaConverters._

class CoreAnalyzer extends Analyzer {
//  override def analyze: Unit = {
//    SentimentAnalyzer.
//  }

  def extractSentiments(inputText: String): Sentiment = {
    Option(inputText.trim) match {
      case Some(text) => {
        val props = new Properties()
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
        val pipeline = new StanfordCoreNLP(props)
        val annotation: Annotation = pipeline.process(text)
        val sentences =
          annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
        val sentiments = sentences.asScala
          .map(
            sentence =>
              (sentence,
               sentence.get(
                 classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
          .map {
            case (sentence, tree) =>
              (sentence.toString, RNNCoreAnnotations.getPredictedClass(tree))
          }
          .toList
        getSentiment(sentiments)
      }
      case _ =>
        throw new IllegalArgumentException("iThe text to be analysed is null")
    }

  }

  def toSentiment(sentiment: Int): Sentiment = sentiment match {
    case x if x == 0 || x == 1 => Sentiment(x, Sentiment.NEGATIVE_SENTIMENT)
    case 2                     => Sentiment(2, Sentiment.NEUTRAL_SENTIMENT)
    case x if x == 3 || x == 4 => Sentiment(x, Sentiment.POSITIVE_SENTIMENT)
  }

  def getSentiment(sentiments: List[(String, Int)]): Sentiment = {
    val maxSentence = sentiments.reduceLeft((a, b) => if (a._2 > b._2) a else b)
    toSentiment(maxSentence._2)
  }

  def main(args: Array[String]): Unit = {
    val str = "I love holidaycheck very much. I use it a lot "
    val result = extractSentiments(str)
    println(result)
  }
}
