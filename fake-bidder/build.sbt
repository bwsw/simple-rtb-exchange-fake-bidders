name := "fake-bidder"

organization := "com.bitworks"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.4",
  "com.github.scopt" %% "scopt" % "3.5.0"
)

assemblyJarName := s"${name.value}-${version.value}.jar"
mainClass in assembly := Some("com.bitworks.rtb.fake.bidder.FakeBidder")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
publishTo := {
  val nexus = "http://rtb-ci.z1.netpoint-dc.com:8081/nexus/content/repositories/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "bitworks-rtb-snapshot/")
  else
    Some("releases" at nexus + "bitworks-rtb/")
}
