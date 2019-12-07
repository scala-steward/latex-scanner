package io.doerfler.latex

trait RegexSupport {
  def Command(name: String) = raw"(?s)\\$name\s*\{(.*?)\}".r

  object Invocations {
    case class of(cmdName: String) {
      def in(text: String) = for (m <- Command(cmdName) findAllMatchIn text) yield m group 1
    }
  }
}