import sbt.Keys._
import sbt._
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.autoImport.{maintainer, packageName}
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.bashScriptDefines
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

import sbtassembly.AssemblyKeys._

object Settings {

  val sdk8 = "adoptopenjdk/openjdk8:x86_64-ubuntu-jdk8u212-b03-slim"
  val sdk11 = "adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.4_11-slim"

  lazy val commonSettings = Seq(
    organization := "com.github.BambooTuna",
    scalaVersion := "2.12.8",
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      ScalaTest.version     % Test,
      ScalaCheck.scalaCheck % Test,
      ScalaMock.version     % Test,
      Akka.testkit     % Test,
      Circe.core,
      Circe.generic,
      Circe.parser,
      Akka.http,
      Akka.stream,
      Akka.persistence,
      Akka.`persistence-query`,
      Akka.cluster,
      Akka.clusterTools,
      Akka.clusterSharding,
      Akka.slf4j,
      Akka.contrib,
      Akka.`akka-http-crice`,
      Logback.classic,
      LogstashLogbackEncoder.encoder,
      Config.core,
      Monix.version
    ),
    scalafmtOnCompile in Compile := true,
    scalafmtTestOnCompile in Compile := true,
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-F", "10")
  )

  lazy val dockerSettings = Seq(
    fork := true,
    name := "commodity-exchange-server",
    version := "latest",
    dockerBaseImage := sdk8,
    maintainer in Docker := "BambooTuna <bambootuna@gmail.com>",
    dockerUpdateLatest := true,
    dockerUsername := Some("bambootuna"),
    mainClass in (Compile, bashScriptDefines) := Some("com.github.BambooTuna.CommodityExchange.apiServer.Main"),
    packageName in Docker := name.value,
    dockerExposedPorts := Seq(8080)
  )

}
