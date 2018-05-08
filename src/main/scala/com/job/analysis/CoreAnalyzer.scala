package com.job.analysis

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

import scala.collection.JavaConverters._

object CoreAnalyzer {
//  override def analyze: Unit = {
//    SentimentAnalyzer.
//  }

  def extractSentiments(text: String): Int = {
    val props = new Properties()
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
    val pipeline = new StanfordCoreNLP(props)
    println("**************************** Processing text : "+ text)
    val annotation: Annotation = pipeline.process(text)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    val sentiments  = sentences.asScala
      .map(sentence => (sentence, sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
      .map { case (sentence, tree) => (sentence.toString,RNNCoreAnnotations.getPredictedClass(tree)) }
      .toList

    getSentiment(sentiments)
  }

  def getSentiment(sentiments : List[(String, Int)]): Int={
      val maxSentence = sentiments.reduceLeft((a, b) => if (a._2>b._2) a else b)
    maxSentence._2
  }

  def main(args: Array[String]): Unit = {
    val str = "I love holidaycheck very much. I use it a lot "
    val result = extractSentiments(str)
    println(result)
  }
}
