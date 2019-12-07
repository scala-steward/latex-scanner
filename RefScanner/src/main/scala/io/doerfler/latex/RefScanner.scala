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
//import scala.concurrent.ExecutionContext.Implicits._
import EC.ec
import scala.util.Failure
import scala.util.Success
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

object EC {
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
}

/** target/universal/stage/bin/refscanner sref Sref cref Cref ref autoref */
object RefScanner extends App with ArgumentParsing with RefScannerLogic {
  def spinner[T](f: Future[T]) = {
    val stream = AnsiConsole.err
    var i = 0
    val spinner = Array('\\', '|', '/', '-')
    stream.print("Searching… ")
    stream.flush()
    while (! f.isCompleted) {
      stream.print(Ansi.ansi().saveCursorPosition())
      stream.print(spinner(i))
      stream.flush()
      stream.print(Ansi.ansi().restoreCursorPosition())
      i = (i + 1) % spinner.size
      Thread.sleep(75)
    }
    stream.print(Ansi.ansi().eraseLine(Ansi.Erase.BACKWARD))
    stream.print("\r")
    stream.flush()

    f
  }

  AnsiConsole.systemInstall()
  parsedArguments(args).map(scan).map(spinner).map { f =>
    f onComplete { 
      case Failure(exception) => throw exception
      case Success((out, err)) => 
        System.out.println(out)
        System.out.flush()
        System.err.println(err)
    }
    EC.ec.shutdown()
  }

}

trait RefScannerLogic extends FileSupport with RegexSupport {
  def scan(cfg: Config): Future[(String, String)] = Future {
    val texSources = for {
      path   <- (Find allFilesEndingWith ".tex" in cfg.workDir ignoring cfg.ignoredTexFiles).par
      source  = new String(Files readAllBytes path)
    } yield source
  
    val allLabels = for {
      oneTexFile <- texSources
      label      <- Invocations of "label" in oneTexFile to Set
    } yield label
  
    val allReferences = for {
      refCmd     <- cfg.refCommands.par
      oneTexFile <- texSources
      ref        <- Invocations of refCmd in oneTexFile to Set
    } yield ref
  
    val unreferenced = for {
      oneUnreferenced    <- allLabels to Set removedAll allReferences
      prettyUnreferenced  = oneUnreferenced replaceAll ("\n+", " ")
    } yield prettyUnreferenced
  
    (unreferenced mkString "\n", s"${unreferenced.size} of ${allLabels.size} labels declared but not referenced.\n${allReferences.size} references total.")
  }
}

trait ArgumentParsing {
  def parsedArguments(args: Array[String]): Option[Config] = {
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
          .action((x, c) => c.copy(refCommands = x to Set))
          .text("The commands that count as referencing commands e.g. ref, autoref, vref, Vref, cref, Cref"),
        opt[Seq[String]]('i', "ignore")
          .optional()
          .valueName("<glob1>,<glob2>")
          .action((x, c) => c.copy(ignoredTexFiles = x to Set))
          .text("List of .tex files that should be ignored"),
      )
    }

    OParser.parse(parser, args, Config())
  }
}

case class Config(
  workDir: String = ".",
  refCommands: Set[String] = Set.empty,
  ignoredTexFiles: Set[String] = Set.empty
)