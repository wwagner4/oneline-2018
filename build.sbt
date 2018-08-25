
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
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.5",
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.7",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.6.6"
  )
  .dependsOn(common)

lazy val server = (project in file("server"))
  .settings(
    name := "oneline-2018-server",
    commonSettings,
    libraryDependencies += "org.scalatra" %% "scalatra" % "2.6.3",
    libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320",
    libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.25",
    libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  )
  .enablePlugins(ScalatraPlugin)
  .dependsOn(core, common)

lazy val common = (project in file("common"))
  .settings(
    name := "oneline-2018-common",
    commonSettings,
  )
