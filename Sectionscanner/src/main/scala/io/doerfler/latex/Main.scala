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
  val allSections = for {
    path <- (Find allFilesEndingWith ".tex" in texPath).par
    content = new String(Files readAllBytes path)
    sections = parse(content, texFile(_)).get.value._2
  } yield path -> sections

  val details = for {
    (p, secs) <- allSections.seq.sortBy(bla => - Progress.within(bla._2))
    if secs.nonEmpty && Progress.within(secs) < 1
  } yield {
    s"${p.getFileName().toString()}: ${f"${Progress.within(secs) * 100}%.0f%%"}"
  }

  val total =
    allSections.map(_._2).map(_.size).sum
  val done =
    allSections.flatMap(_._2).filter(_.status == "done").size
  val todo = total - done
  val overalFraction = Progress.within(allSections.flatMap(_._2).seq)
  val overalPercentage = f"${overalFraction * 100}%.0f%%"
  val msg = f"$done%d of $total%d sections done, $todo%d left"

  def percentBar(space: Int, fraction: Double) = {
    f"${bar(space, fraction)}%s ${fraction * 100}%3.0f%%"
  }

  def bar(space: Int, fraction: Double) = {
    val doneF = math.ceil(space * fraction).toInt
    val todoF = space - doneF
    
    ("▓" repeat doneF) + ("░" repeat todoF)
  }

  val detailsBars = for {
    (p, secs) <- allSections.seq.sortBy(bla => - Progress.within(bla._2))
    if secs.nonEmpty
    done = secs.filter(_.status == "done").size
    todo = secs.filter(_.status != "done").size
  } yield f"${percentBar(10, Progress.within(secs))}%s ${p.getFileName().toString()}%s"

  val longBar = bar(msg.length - 5, overalFraction)
  val shortBar = bar(11, overalFraction) // 11 is the largest you can go with a status icon and spotify adding even more description to the status

  if (details.nonEmpty) {
    println(details.mkString("Progress in non completed files:\n--------------------------------\n- ", "\n- ", ""))
    println()
  }
  if (detailsBars.nonEmpty) {
    println(detailsBars.mkString("Progress in all files:\n----------------------\n- ", "\n- ", ""))
    println(msg)
    println()
  }
  println("Progress overall: " + longBar + f" ${overalFraction * 100}%3.0f%%")
  println()
  println("Progress Discord: " + shortBar + f" ${overalFraction * 100}%2.0f%%")
  // Seq("bmndr", "sections", todo.toString).!
  
  // for {
  //   as <- allSections.seq
  //   (f, secs) = as
  //   _ = println(s"$f:")
  // } for {
  //     s <- secs
  //   } {
  //     println(s"${" " * s.level * 2}- ${s.name}, ${s.status}, ${s.content.length}")
  //   }
}