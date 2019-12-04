package io.doerfler.latex

trait RegexSupport {
  def Command(name: String) = raw"(?s)\\$name\s*\{(.*?)\}".r

  object Invocations {
    def of(cmdName: String) = new {
      def in(text: String) = for (m <- Command(cmdName) findAllMatchIn text) yield m group 1
    }
  }
}