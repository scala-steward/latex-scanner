package io.doerfler.latex

import scala.io.Source
import java.io.File

import scala.collection.parallel.CollectionConverters._

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Path
import scala.jdk.StreamConverters._
import scala.collection.parallel.ParSeq
import scala.collection.parallel.immutable.ParSet

/** target/universal/stage/bin/refscanner sref Sref cref Cref ref textref */
object RefScanner extends App with FileSupport with RegexSupport {
  val texSources = for {
    path   <- (Files find (Paths get ".", 255, texFiles) toScala LazyList).par
    source  = new String(Files readAllBytes path)
  } yield source

  val allLabels = for {
    oneTexFile <- texSources
    label      <- Invocations of "label" in oneTexFile to Set
  } yield label

  val allReferences = for {
    refCmd     <- (args to Set).par
    oneTexFile <- texSources
    ref        <- Invocations of refCmd in oneTexFile to Set
  } yield ref

  val unreferenced = for {
    oneUnreferenced    <- allLabels to Set removedAll allReferences
    prettyUnreferenced  = oneUnreferenced replaceAll ("\n+", " ")
  } yield prettyUnreferenced

  unreferenced foreach println
  System.out.flush()
  val stats = s"${unreferenced.size} of ${allLabels.size} labels declared but not referenced.\n${allReferences.size} references total."
  System.err.println(stats)
}

trait RegexSupport {
  def Command(name: String) = raw"(?s)\\$name\s*\{(.*?)\}".r

  object Invocations {
    def of(cmdName: String) = new {
      def in(text: String) = for (m <- Command(cmdName) findAllMatchIn text) yield m group 1
    }
  }
}

trait FileSupport {
  def texFiles(p: Path, bfa: BasicFileAttributes) = p.toString.toLowerCase endsWith ".tex"
}