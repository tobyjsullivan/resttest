// Dependencies
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"

lazy val root = (project in file(".")).
  settings(
    name := "resttest",
    version := "1.0",
    scalaVersion := "2.12.1",

    libraryDependencies ++= Seq(
      scalaTest
    )
  )
