package com.github.lambdas.ensimegradle

import cats.Show
import cats.syntax.show._
import com.github.lambdas.ensimegradle.show._

case class EnsimeProjectId(project: String, config: String)

object EnsimeProjectId {
  implicit val ensimeProjectIdShow: Show[EnsimeProjectId] = {
    Show.show { p => show"(:project ${p.project} :config ${p.config})" }
  }
}
