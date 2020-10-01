package io.doerfler.latex.parser

import io.doerfler.latex._

import fastparse._, NoWhitespace._

trait ParserSupport extends GeneralParserSupport {
  def texFile[_:P] = P( contentBeforeFirstSection ~ sectionWithContent.rep )
  def contentBeforeFirstSection[_:P] = P( notSection )
  def contentAfterSection[_:P] = P( notSection )
  def notSection[_:P] = ( (!section ~ AnyChar).rep.! )
  def sectionWithContent[_:P] = P(section ~ contentAfterSection map Section.tupled)
  def section[_: P] = P( "\\" ~ level ~ "{" ~/ (AnyString ~? "\\status") ~ status.! ~ "{}" ~ "}" )

  def level[_: P] = P(sectionL | subSectionL | subSubSectionL)
  def sectionL[_:P] = P("section") map (_ => 1)
  def subSectionL[_:P] = P("subsection") map (_ => 2)
  def subSubSectionL[_:P] = P("subsubsection") map (_ => 3)
  //
  def status[_: P] = P(CharIn("a-zA-Z").rep)
  def todo[_:P] = P(must | maybe)
  def must[_:P] = P("\\" ~ "todo" ~ "{" ~/ (AnyString ~? "}"))
  def maybe[_:P] = P("\\" ~ "maybe" ~ "{" ~/ (AnyString ~? "}"))
  //
  def stuff[_:P]: P[Seq[CmdOrText]] = P( textOrCmd.rep )
  def textOrCmd[_:P]: P[CmdOrText] = P( cmd | text )
  def cmd[_:P]: P[CmdOrText] = P("\\" ~ cmdName ~ "{" ~/ stuff ~ "}" map (Cmd.apply _).tupled )
  def cmdName[_:P]: P[String] = P( CharIn("a-zA-Z0-9").rep.! )
  def text[_:P]: P[CmdOrText] = P( (CharIn("a-zA-Z0-9 \n\r\t") | "\\&").rep(1).! map Text )
}

trait GeneralParserSupport {
  case object AnyString {
    def ~?[_:P](termination: String): P[String] = P( (!termination ~ AnyChar).rep.! ~ termination )
  }

  def verbose[T](res: Parsed[T]): Either[String, T] =
    res.fold((s, i, e) => Left(e.trace().longAggregateMsg), (t, i) => Right(t))

  def verboser[T](res: Parsed[T]): Either[String, T] =
  res.fold((l, i, e) => Left("\n" + e.input.asInstanceOf[IndexedParserInput].data + "\n" + (" " * i) + "^" + "\n" + e.trace().longAggregateMsg), (t, i) => Right(t))

  def toOption[T](res: Parsed[T]): Option[T] =
    res.fold((s, i, e) => None, (t, i) => Some(t))
}