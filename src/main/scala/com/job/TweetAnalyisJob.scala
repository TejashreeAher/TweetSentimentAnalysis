package com.job

import org.apache.spark.sql.SparkSession

object TweetAnalyisJob {
  def main(args: Array[String]): Unit = {
    val sc = SparkSession
      .builder()
      .appName("tweet-analyser")
      .getOrCreate()
  }

}
