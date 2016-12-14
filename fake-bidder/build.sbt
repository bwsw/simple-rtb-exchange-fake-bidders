name := "fake-bidder"

organization := "com.bitworks"

version := "1.1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.4",
  "com.github.scopt" %% "scopt" % "3.5.0",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11"
)

assemblyJarName := s"${name.value}-${version.value}-jar-with-dependencies.jar"
mainClass in assembly := Some("com.bitworks.rtb.fake.bidder.FakeBidder")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
publishTo := {
  val nexus = "http://rtb-nexus/nexus/content/repositories/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "bitworks-rtb-snapshot/")
  else
    Some("releases" at nexus + "bitworks-rtb/")
}

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("jar-with-dependencies"))
}

addArtifact(artifact in (Compile, assembly), assembly)
