
val root = project
  .in(file("."))
  .settings(
    name := "tweet-sentiment-anaysis",
    version := "0.0.1",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Dependencies.root,
    assemblyMergeStrategy in assembly := {
    case PathList("log4j.xml", xs @ _ *)      => MergeStrategy.discard
    case PathList("reference.conf", xs @ _ *) => MergeStrategy.concat
    case PathList("META-INF", xs @ _ *)       => MergeStrategy.discard
    case x                                    => MergeStrategy.first
  }
  )
