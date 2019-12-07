ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "1.0-SNAPSHOT"
ThisBuild / organization     := "io.doerfler"
ThisBuild / organizationName := "Philipp Dörfler"
ThisBuild / crossPaths       := false

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .aggregate(cmdscanner, refscanner, common)

lazy val refscanner = appProject("refscanner", file("Refscanner"))
  .dependsOn(common)
lazy val cmdscanner = appProject("cmdscanner", file("Cmdscanner"))
  .dependsOn(common)
lazy val common = (project in file("Common"))
  .settings(commonSettings)

def appProject(id: String, base: File) =
  Project(id, base)
    .settings(commonSettings, graalVmSettings, buildInfoSettings)
    .enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin, BuildInfoPlugin)

lazy val commonSettings: List[Setting[_]] = List(
  scalacOptions := Seq("-unchecked", "-deprecation", "-language:_", "-encoding", "UTF-8", "-target:jvm-1.8"),
  libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0",
  libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.0-RC2",
  libraryDependencies += "org.fusesource.jansi" % "jansi" % "1.17.1",
  libraryDependencies += "jline" % "jline" % "2.14.6"
)

lazy val buildInfoSettings: Seq[Setting[_]] = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, executableScriptName),
  buildInfoPackage := "io.doerfler.latex"
)

lazy val graalVmSettings: Seq[Setting[_]] = List(
  graalVMNativeImageOptions ++= Seq(
    "-H:+ReportExceptionStackTraces",
    "-H:+TraceClassInitialization",
    "--no-fallback",
    "--verbose",
    "--report-unsupported-elements-at-runtime",
    "--allow-incomplete-classpath",
    "--initialize-at-build-time")
)