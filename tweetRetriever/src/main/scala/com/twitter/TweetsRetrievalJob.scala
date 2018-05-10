package com.twitter

import org.apache.spark.sql.SparkSession

object TweetsRetrievalJob {
  val JOB_NAME = "Tweet Retrieval"
  case class Config(twitterConfigFilePath: String = "", startdate: String = "")

  def main(args: Array[String]): Unit = {
    val defaultArgs = Config()
    val parser = new scopt.OptionParser[Config]("scopt") {
      head("parser")
      opt[String]('f', "config-file-path")
          .required()
            .text("Path of the file with twitter credentials")
        .action((x, c) => ///use -f or --config-file-path
        c.copy(twitterConfigFilePath = x))

      opt[String]('d', "start-date")
          .required()
          .text("start date from which tweets are to be fetched")
        .action((x, c) =>
        c.copy(startdate = x))
    }
    parser.parse(args, defaultArgs)
        .map{config =>
          println(s"Config file path is : ${config.twitterConfigFilePath}")
          val sc = getSparkContext(JOB_NAME)
        }
      .getOrElse(
        System.exit(0)
      )



  }

  def getSparkContext(name: String)={
    val spark = SparkSession.builder.appName(name)
      .master("local[2]")
      .getOrCreate()
  }
}
