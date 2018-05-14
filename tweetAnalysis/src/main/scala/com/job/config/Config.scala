package com.job.config

import com.typesafe.config.{Config, ConfigFactory}

//write test
object Configuration {

  def apply(): Config = {
    val config = ConfigFactory.load(s"application.conf")
    config
  }
}

case class InputConfig(filePath: String)
case class OutpurConfig(analysedPathPrefix: String,
                        aggregatedPathPrefix: String)
