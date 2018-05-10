object TweetRetriever {
  case class Config(twitterConfigFilePath : String)

  def main(args: Array[String]): Unit = {
    val defaultConfig = Config("")
    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")
      opt[String]('f', "configFile") required() action { (x, c) =>
        c.copy(twitterConfigFilePath = x) } text("config file for twitter api")
    }

    val config = parser.parse(args, defaultConfig)
      .getOrElse{
        parser.showUsageAsError
        sys.exit(1)
      }

    println("Config file path : "+ config.twitterConfigFilePath)
  }

}
