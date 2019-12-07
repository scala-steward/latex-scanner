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

trait SpinnerSupport {
  AnsiConsole.systemInstall()
  case object monitor

  object Spinner {
    case class show(message: String) {
      def whileWaitingFor[T](f: Future[T]): Unit = {
        val f2 = f andThen { case _ => monitor synchronized monitor.notifyAll }
        val stream = AnsiConsole.err
        var i = 0
        val spinner = Array('\\', '|', '/', '-')
        stream.print(f"$message%s  ")
        stream.flush()
        while (! f.isCompleted) {
          stream.print("\b")
          stream.print(spinner(i))
          stream.flush()
          i = (i + 1) % spinner.size
          monitor synchronized { if (! f2.isCompleted) monitor wait 75 }
        }
        stream.print(Ansi.ansi().eraseLine(Ansi.Erase.BACKWARD))
        stream.print("\r")
        stream.flush()
      }
    }
  }
}