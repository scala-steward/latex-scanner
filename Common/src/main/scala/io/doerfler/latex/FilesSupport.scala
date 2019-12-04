package io.doerfler.latex

import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Files
import java.nio.file.Paths
import scala.jdk.StreamConverters._

trait FileSupport {
  object Find {
    def allFilesEndingWith(ending: String) = new {
      def in(dir: String) = {
        def filesWithSpecificEnding(p: Path, bfa: BasicFileAttributes) =
          p.toString.toLowerCase endsWith ending

        Files find (Paths get dir, 255, filesWithSpecificEnding) toScala LazyList
      }
    }
  }
}