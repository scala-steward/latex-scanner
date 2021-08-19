package io.doerfler

import java.nio.file.Path

package object latex {
  object Implicits {
    implicit class RichPaths(val files: Iterable[Path]) extends AnyVal {
      def ignoring(ignored: Set[String]): Iterable[Path] = {
        files filterNot (f => ignored contains (f.getFileName toString))
      }
    }
  }
}
