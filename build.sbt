import Versions.*
import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.binarycamp"

lazy val demo = (project in file("."))
  .aggregate(app, service)

lazy val api = (crossProject(JVMPlatform, JSPlatform) in file("api"))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %%% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %%% "tapir-jsoniter-scala" % tapirVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % jsoniterVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-macros" % jsoniterVersion % Provided
    )
  )

lazy val app = (project in file("app"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(api.js)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { config =>
      config
        .withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("demo")))
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
      "com.raquo" %%% "laminar" % laminarVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % jsoniterVersion,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-macros" % jsoniterVersion % Provided,
      "com.softwaremill.sttp.tapir" %%% "tapir-sttp-client" % tapirVersion
    )
  )

lazy val service = (project in file("service"))
  .dependsOn(api.jvm)
  .settings(
    Compile / run / fork := true,
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "org.http4s" %% "http4s-blaze-server" % blazeServerVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion % Runtime
    )
  )
