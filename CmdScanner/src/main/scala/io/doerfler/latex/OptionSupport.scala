package io.doerfler.latex

import scala.io.Source
import java.io.File

import scala.collection.parallel.CollectionConverters._

import scala.collection.parallel.ParSeq
import scala.collection.parallel.immutable.ParSet
import java.nio.file.Files

import java.nio.file.Path

import scopt.OParser

import io.doerfler.latex.Implicits._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.Failure
import scala.util.Success
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

trait OptionSupport {
  def parse(args: Array[String]): Option[Config] = {
    val builder = OParser.builder[Config]
    val parser = {
      import builder._
      OParser.sequence(
        head(BuildInfo.executableScriptName, BuildInfo.version),
        programName(BuildInfo.executableScriptName),
        arg[File]("<directory>")
          .optional()
          .text("Directory tree to search for .tex files in")
          .action((f, c) => c.copy(workDir = f.getPath())),
        opt[Seq[String]]('c', "commands")
          .required()
          .valueName("<cmd1>,<cmd2>")
          .action((x, c) => c.copy(commands = x to Set))
          .text("The commands to look for"),
        opt[Seq[String]]('i', "ignore")
          .optional()
          .valueName("<glob1>,<glob2>")
          .action((x, c) => c.copy(ignoredTexFiles = x to Set))
          .text("List of .tex files that should be ignored")
      )
    }

    OParser.parse(parser, args, Config())
  }
}

case class Config(
    workDir: String = ".",
    commands: Set[String] = Set.empty,
    ignoredTexFiles: Set[String] = Set.empty
)
