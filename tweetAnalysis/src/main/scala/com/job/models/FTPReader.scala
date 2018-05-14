package com.job.models

import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.{SparkConf, SparkContext}

object FTPReader {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf()
        .setAppName("ftp reader")
        .setMaster("local[2]"))

//    val fileRead = sc.wholeTextFiles("ftp://xings:rcY$b8PK@ftp3.omniture.com/xingstaging_2018-05-10-lookup_data.tar.gz")
//      .values.saveAsTextFile("/tmp/lookup_data_whole_zip")

    val fileRead = sc.wholeTextFiles("ftp://xings:rcY$b8PK@ftp3.omniture.com/xingstaging_2018-05-10-lookup_data.tar.gz")
        .values
        .saveAsTextFile("/tmp/lookup_data_whole_key_value", classOf[GzipCodec])



  }

}
