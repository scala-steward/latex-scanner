import Dependencies._

ThisBuild / scalaVersion     := "2.13.0" // "2.13.0"
ThisBuild / version          := "1.0-SNAPSHOT"
ThisBuild / organization     := "io.doerfler"
ThisBuild / organizationName := "Philipp Dörfler"
ThisBuild / crossPaths := false

Global / onChangedBuildSource := ReloadOnSourceChanges

name := "cmdscanner"

scalacOptions := Seq("-unchecked", "-deprecation", "-language:_", "-encoding", "UTF-8", "-target:jvm-1.8")

//libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0"

enablePlugins(JavaAppPackaging)
enablePlugins(GraalVMNativeImagePlugin)
graalVMNativeImageOptions ++= Seq("--no-fallback", "--report-unsupported-elements-at-runtime", "-H:+ReportExceptionStackTraces")

val hackyMcHackFace = require(System.getProperty("java.version").startsWith("1.8"))