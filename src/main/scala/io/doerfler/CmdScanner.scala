package io.doerfler

import scala.io.Source
import java.io.File

import scala.collection.parallel.CollectionConverters._

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.FileVisitOption
import java.nio.file.Path
import scala.jdk.StreamConverters._

/** target/universal/stage/bin/cmdscanner emph | sort -f | uniq -di */
object CmdScanner extends App with FileSupport with RegexSupport {
  val items = for {
    path        <- (Files find (Paths get ".", 255, texFiles) toScala LazyList).par
    content      = new String(Files readAllBytes path)
    cmdName      = args lift 0 getOrElse "todo"
    oneMatch    <- matchesIn(cmdName, content) to Vector
    prettyMatch  = oneMatch.replaceAll("\n+", " ")
  } yield prettyMatch

  items foreach println
}

trait RegexSupport {
  def Command(name: String) = raw"(?s)\\$name\s*\{(.*?)\}".r

  def matchesIn(cmdName: String, s: String) = for (m <- Command(cmdName) findAllMatchIn s) yield m group 1
}

trait FileSupport {
  def texFiles(p: Path, bfa: BasicFileAttributes) = p.toString.toLowerCase endsWith ".tex"
}