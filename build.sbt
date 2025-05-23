ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version := "1.0-SNAPSHOT"
ThisBuild / organization := "io.doerfler"
ThisBuild / organizationName := "Philipp Dörfler"
ThisBuild / crossPaths := false

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .aggregate(cmdscanner, refscanner, sectionscanner, common)

lazy val sectionscanner = appProject("sectionscanner", file("Sectionscanner"))
  .dependsOn(common)
  .settings(
    initialCommands in (Compile, console) := """import io.doerfler.latex._
                                                |import Main._""".stripMargin
  )

lazy val footnotescanner =
  appProject("footnotescanner", file("Footnotescanner"))
    .dependsOn(common)

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
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-language:_",
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8"
  ),
  libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
  libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0",
  libraryDependencies += "org.fusesource.jansi" % "jansi" % "2.4.2",
  libraryDependencies += "jline" % "jline" % "2.14.6",
  libraryDependencies += "com.lihaoyi" %% "fastparse" % "3.1.1"
)

lazy val buildInfoSettings: Seq[Setting[_]] = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    executableScriptName
  ),
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
    "--initialize-at-build-time"
  )
)
