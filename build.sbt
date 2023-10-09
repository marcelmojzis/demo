import Versions.*

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.binarycamp"

lazy val demo = (project in file("."))
  .aggregate(service)

lazy val api = (project in file("api"))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-jsoniter-scala" % tapirVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsoniterVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion % Provided
    )
  )

lazy val service = (project in file("service"))
  .dependsOn(api)
  .settings(
    Compile / run / fork := true,
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "org.http4s" %% "http4s-blaze-server" % blazeServerVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion % Runtime
    )
  )
