lazy val commonSettings = Seq(
  organization := "net.entelijan",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .aggregate(oneline_core, oneline_client, oneline_server)

lazy val oneline_core = (project in file("core"))
  .settings(
    commonSettings,
    name := "oneline-2018-core",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

lazy val oneline_client = (project in file("client"))
  .settings(
    name := "oneline-2018-client",
    commonSettings,
  )
  .dependsOn(oneline_core)

lazy val oneline_server = (project in file("server"))
  .settings(
    name := "oneline-2018-server",
    commonSettings,
  )
  .dependsOn(oneline_core)
