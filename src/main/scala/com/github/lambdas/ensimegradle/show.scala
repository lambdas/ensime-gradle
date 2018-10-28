package com.github.lambdas.ensimegradle

import java.nio.file.Path

import cats.Show
import cats.syntax.show._

object show {
  implicit val stringShow: Show[String] = Show.show("\"" + _ + "\"")
  implicit val pathShow: Show[Path] = Show.show("\"" + _.toString + "\"")
  implicit def seqShow[A](implicit ev: Show[A]): Show[Seq[A]] = Show.show("(" + _.map(_.show).mkString(" ") + ")")
  implicit def setShow[A](implicit ev: Show[A]): Show[Set[A]] = Show.show(_.toSeq.show)
}
