import sbt._

lazy val tweetRetriever = project
.in(file("tweetRetriever"))
.settings(
    name := "tweet-retriever",
    version := "0.0.1",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Dependencies.dataRetriever,
    assemblyMergeStrategy in assembly := {
        case PathList("log4j.xml", xs @ _ *)      => MergeStrategy.discard
        case PathList("reference.conf", xs @ _ *) => MergeStrategy.concat
        case PathList("META-INF", xs @ _ *)       => MergeStrategy.discard
        case x                                    => MergeStrategy.first
    }
)

lazy val tweetAnalysis = project
  .in(file("tweetAnalysis"))
  .settings(
    name := "tweet-sentiment-analysis",
    version := "0.0.1",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Dependencies.analysisJob,
    assemblyMergeStrategy in assembly := {
      case PathList("log4j.xml", xs @ _ *)      => MergeStrategy.discard
      case PathList("reference.conf", xs @ _ *) => MergeStrategy.concat
      case PathList("META-INF", xs @ _ *)       => MergeStrategy.discard
      case x                                    => MergeStrategy.first
    }
  )


val root = project
  .in(file("."))
  .settings(
    name := "sentiment-analysis-job"
  )
  .aggregate(tweetRetriever, tweetAnalysis)
