package com.twitter.mapper

import org.apache.flink.api.common.functions.MapFunction


class HdfsEncoder() extends MapFunction[Tweet, String]{
  override def map(value: Tweet): String = {
    StringBuilder.newBuilder
      .append(value.id)
      .append("\t")
      .append(value.text)
      .append("\t")
      .append(value.createdAt)
      .append("\t")
      .append(value.favoriteCount)
      .toString()
  }
}
