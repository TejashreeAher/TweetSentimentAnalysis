
val root = project
  .in(file("."))
  .settings(
    name := "tweet-sentiment-anaysis",
    version := "0.0.1",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Dependencies.root
  )
