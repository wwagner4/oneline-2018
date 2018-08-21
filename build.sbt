lazy val commonSettings = Seq(
  organization := "net.entelijan",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .aggregate(core, client, server)

lazy val core = (project in file("core"))
  .settings(
    name := "oneline-2018-core",
    commonSettings,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "oneline-2018-client",
    commonSettings,
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.6",
    libraryDependencies += "org.querki" %%% "jquery-facade" % "1.2",
  )
  .dependsOn(core)

lazy val server = (project in file("server"))
  .settings(
    name := "oneline-2018-server",
    commonSettings,
  )
  .dependsOn(core)
