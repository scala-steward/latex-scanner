package io.doerfler.latex

sealed trait Ast
case class Section(level: Int, name: String, status: String, content: String) extends Ast

case class ToDo(kind: TodoKind, text: String) extends Ast
sealed trait TodoKind
case object Must extends TodoKind
case object Maybe extends TodoKind

sealed trait CmdOrText
case class Cmd(name: String, textOrCmd: Seq[CmdOrText]) extends CmdOrText
case class Text(value: String) extends CmdOrText
