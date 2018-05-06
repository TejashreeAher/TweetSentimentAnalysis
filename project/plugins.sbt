resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal)

addSbtPlugin("org.scalastyle"    %% "scalastyle-sbt-plugin" % "0.8.0")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"           % "0.6.8")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"          % "1.5.0")
addSbtPlugin("com.eed3si9n"      % "sbt-assembly"           % "0.14.3")