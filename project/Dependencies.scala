import sbt.{ExclusionRule, _}

object Dependencies {
  object Versions {
    val spark     = "2.2.1"
  }

  object Spark {
    val all = Seq("org.apache.spark" %% "spark-core" % Versions.spark,
                  "org.apache.spark" %% "spark-sql"  % Versions.spark,
//                  "org.twitter4j" % "twitter4j-core" % "4.0.6",
                  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2" artifacts
                    (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")))
  }

  object Logging {
    val all =
      Seq("com.typesafe.scala-logging" %% "scala-logging" % "3.8.0")
//          "ch.qos.logback" % "logback-classic" % "1.2.3",
//          "com.github.pureconfig" %% "pureconfig" % "0.9.0")
  }

  object Testing {
    val all = Seq("org.scalatest" %% "scalatest" % "3.0.4" % Test excludeAll(ExclusionRule("com.fasterxml.jackson.core", "jackson-core")),
      "com.github.tomakehurst" % "wiremock" % "2.14.0" % Test excludeAll(
        ExclusionRule("com.fasterxml.jackson.core", "jackson-core"),
        ExclusionRule("com.fasterxml.jackson.core", "jackson-databind")
      ))
  }
  val root = Spark.all ++ Logging.all ++ Testing.all

}
