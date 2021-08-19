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
object CmdScanner
    extends App
    with OptionSupport
    with FileSupport
    with RegexSupport {
  parse(args).map(scan).foreach { items =>
    items.foreach(println)
  }

  def scan(config: Config) = {
    val items = for {
      path <- (Find allFilesEndingWith ".tex" in config.workDir).par
      if !config.ignoredTexFiles.contains(path.getFileName.toString)
      content = new String(Files readAllBytes path)
      cmdName <- config.commands
      oneMatch <- Invocations of cmdName in content to Vector
      prettyMatch = oneMatch replaceAll ("\n+", " ")
    } yield path -> prettyMatch

    items map { case (p, m) => s"${p.getFileName}: $m" }
  }
}
