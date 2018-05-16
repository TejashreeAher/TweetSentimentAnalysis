package com.twitter.functions

import com.twitter.Utils
import org.apache.flink.streaming.connectors.fs.Clock
import org.apache.flink.streaming.connectors.fs.bucketing.DateTimeBucketer
import org.apache.hadoop.fs.Path

class TweetBucketer(formatString: String) extends DateTimeBucketer[String] {

  override def getBucketPath(clock: Clock,
                             basePath: Path,
                             element: String): Path = {
    val newDateTimeString = Utils.parseDate(element.split(",")(1))
    println(s"Date bucket is ${newDateTimeString}")
    new Path(basePath + "/" + newDateTimeString)
  }
}
