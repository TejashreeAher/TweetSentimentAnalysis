package com.twitter.mapper

import org.apache.flink.api.common.functions.MapFunction


class HdfsEncoder() extends MapFunction[Tweet, String]{
  override def map(value: Tweet): String = {
    StringBuilder.newBuilder
      .append(value.id)
      .append(",")
      .append(value.createdAt)
      .append(",")
      .append(s""""${value.text}"""")
      .append(",")
      .append(value.favoriteCount)
      .toString()
  }
}
