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

trait ScanningSupport extends FileSupport with RegexSupport {
  def scan(cfg: Config): Future[(String, String)] = Future {
    val texSources = for {
      path <-
        (Find allFilesEndingWith ".tex" in cfg.workDir ignoring cfg.ignoredTexFiles).par
      source = new String(Files readAllBytes path)
    } yield source

    val allLabels = for {
      oneTexFile <- texSources
      label <- Invocations of "label" in oneTexFile to Set
    } yield label

    val allReferences = for {
      refCmd <- cfg.refCommands.par
      oneTexFile <- texSources
      ref <- Invocations of refCmd in oneTexFile to Set
    } yield ref

    val unreferenced = for {
      oneUnreferenced <- allLabels to Set removedAll allReferences
      prettyUnreferenced = oneUnreferenced replaceAll ("\n+", " ")
    } yield prettyUnreferenced

    val out = unreferenced mkString "\n"
    val err = f"""
      |${unreferenced.size}%d of ${allLabels.size}%d labels declared but not referenced.
      |${allReferences.size}%d references total.
    """.stripMargin('|').trim

    (out, err)
  }
}
