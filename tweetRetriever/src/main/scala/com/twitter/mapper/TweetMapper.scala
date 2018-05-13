package com.twitter.mapper

import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.util.Collector

case class Tweet(id: Long, createdAt: String, text: String, favoriteCount: Int=0)
class TweetMapper extends FlatMapFunction[String, Tweet] {
  val mapper = new ObjectMapper()
  override def flatMap(value: String, out: Collector[Tweet]): Unit = {
    try{
      val jsonRecvd = mapper.readValue(value, classOf[JsonNode])
      mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
      if(jsonRecvd.has("created_at") && isValidTweet(jsonRecvd))
      {
        println(s"JSON IS ******************* -> ${value} ")
        val id = jsonRecvd.get("id").asLong()
        val createdAt = jsonRecvd.get("created_at").asText()
        val text = jsonRecvd.get("text").asText()
        val favoriteCount = jsonRecvd.get("favorite_count").asInt()
        out.collect(Tweet(id, createdAt, text, favoriteCount))
      }
    }catch{
      case e: Exception => println(s"Unable to parse Json : ${value} with exception : ${e.getMessage}")
    }
  }

  def isValidTweet(tweet: JsonNode)={
    true
  }
}

