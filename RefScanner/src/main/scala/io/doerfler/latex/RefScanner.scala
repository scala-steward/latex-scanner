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

/**
 * $ sbt refscanner/stage
 * target/universal/stage/bin/refscanner -c cref,Cref,ref,autoref
 * 
 * $ sbt refscanner/graalvm-native-image:packageBin
 * Refscanner/target/graalvm-native-image/refscanner -c cref,Cref,ref,autoref
 */
object RefScanner extends App with ParsingSupport with ScanningSupport with SpinnerSupport {
  parse(args).map(scan).foreach { scanF =>
    Spinner show "Searching…" whileWaitingFor scanF
    val (out, err) = Await.result(scanF, Duration.Inf)
    System.out.println(out)
    System.out.flush()
    System.err.println(err)
  }
}