import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}



lazy val commonSettings = Seq(
  // scalacOptions := Seq("-unchecked", "-deprecation"),
  organization := "net.entelijan",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .settings(
    name := "oneline-2018",
    commonSettings)
  .aggregate(core, common.js, common.jvm, client, server)

lazy val core = (project in file("core"))
  .settings(
    name := "oneline-2018-core",
    commonSettings,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )

lazy val client = (project in file("client"))
  .settings(
    name := "oneline-2018-client",
    commonSettings,
    scalaJSUseMainModuleInitializer := true,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.5",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.6.6",
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.6.5" % "test",
  )
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(common.js)

lazy val server = (project in file("server"))
  .settings(
    name := "oneline-2018-server",
    commonSettings,
    fork in run := true,
    mainClass in assembly := Some("oneline.server.JettyStarter"),
    assemblyJarName in assembly := "oneline.jar",
    libraryDependencies += "org.scalatra" %% "scalatra" % "2.6.3",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
    libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320",
  )
  .dependsOn(core, common.jvm)

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("common"))
  .settings(
    name := "oneline-2018-common",
    commonSettings,
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.6.6",
  )
