package com.twitter.mapper

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.util.Collector


class TextProcessor extends FlatMapFunction[Tweet, Tweet]{
  override def flatMap(value: Tweet, out: Collector[Tweet]): Unit = {
    println(s"In text processor to process ${value.toString}")
    out.collect(Tweet(value.id, value.createdAt, value.text.replaceAll("(\r\n)|\r|\n", ""), value.favoriteCount))
  }
}
