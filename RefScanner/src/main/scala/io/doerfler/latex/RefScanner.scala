package io.doerfler.latex

import scala.io.Source
import java.io.File

import scala.collection.parallel.CollectionConverters._

import scala.collection.parallel.ParSeq
import scala.collection.parallel.immutable.ParSet
import java.nio.file.Files

/** target/universal/stage/bin/refscanner sref Sref cref Cref ref autoref */
object RefScanner extends App with FileSupport with RegexSupport {
  val refCmds = args to Set

  val texSources = for {
    path   <- (Find allFilesEndingWith ".tex" in ".").par
    source  = new String(Files readAllBytes path)
  } yield source

  val allLabels = for {
    oneTexFile <- texSources
    label      <- Invocations of "label" in oneTexFile to Set
  } yield label

  val allReferences = for {
    refCmd     <- refCmds.par
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