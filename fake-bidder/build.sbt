name := "fake-bidder"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.4",
  "com.github.scopt" %% "scopt" % "3.5.0"
)
