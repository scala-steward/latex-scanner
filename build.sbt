ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "1.0-SNAPSHOT"
ThisBuild / organization     := "io.doerfler"
ThisBuild / organizationName := "Philipp Dörfler"
ThisBuild / crossPaths       := false

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .aggregate(cmdScanner, refScanner)

lazy val refScanner = (project in file("RefScanner"))
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)

lazy val cmdScanner = (project in file("CmdScanner"))
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)

lazy val commonSettings: List[Setting[_]] = List(
  scalacOptions := Seq("-unchecked", "-deprecation", "-language:_", "-encoding", "UTF-8", "-target:jvm-1.8"),
  libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0",

  graalVMNativeImageOptions ++= Seq("--no-fallback", "--report-unsupported-elements-at-runtime", "-H:+ReportExceptionStackTraces")
)