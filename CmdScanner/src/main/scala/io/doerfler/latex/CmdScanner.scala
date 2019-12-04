package io.doerfler.latex

import scala.io.Source
import java.io.File

import scala.collection.parallel.CollectionConverters._

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Path
import scala.jdk.StreamConverters._

/** target/universal/stage/bin/cmdscanner emph | sort -f | uniq -di */
object CmdScanner extends App with FileSupport with RegexSupport {
  val items = for {
    path        <- (Find allFilesEndingWith ".tex" in ".").par
    content      = new String(Files readAllBytes path)
    cmdName     <- args lift 0 to Vector
    oneMatch    <- Invocations of cmdName in content to Vector
    prettyMatch  = oneMatch.replaceAll("\n+", " ")
  } yield prettyMatch

  items foreach println
}