package io.doerfler.latex

import fastparse._, NoWhitespace._
import io.doerfler.latex.parser._
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Path
import scala.jdk.StreamConverters._
import scala.collection.parallel.CollectionConverters._
import scala.sys.process._

object Main extends App with FileSupport with ParserSupport {
  import Ordering.Float.TotalOrdering

  val texPath = args.lift(0).getOrElse(".")
  val allFootnotes = for {
    path <- (Find allFilesEndingWith ".tex" in texPath).par
    content = new String(Files readAllBytes path)
    footnotes = verbose(parse(content, stuff(_)))
  } yield path -> footnotes
  allFootnotes.foreach(println)

}
