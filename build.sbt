name := "MarsRover"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/" //for sbt-assembly

libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )
}

assemblyJarName in assembly := "rovers-simulator.jar"

mainClass in assembly := Some("org.brickx.challenge.rovers.RoversController")