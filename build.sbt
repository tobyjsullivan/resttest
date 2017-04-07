// Dependencies
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
val jodaTime = "joda-time" % "joda-time" % "2.9.9"
val sprayJson = "io.spray" %% "spray-json" % "1.3.3"
val scalajHttp = "org.scalaj" %% "scalaj-http" % "2.3.0"

lazy val root = (project in file(".")).
  settings(
    name := "resttest",
    version := "1.0",
    scalaVersion := "2.12.1",

    libraryDependencies ++= Seq(
      scalaTest,
      jodaTime,
      sprayJson,
      scalajHttp
    )
  )
