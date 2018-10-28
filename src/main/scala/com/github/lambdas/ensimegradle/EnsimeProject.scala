package com.github.lambdas.ensimegradle

import java.nio.file.Path

import cats.Show
import cats.syntax.show._
import com.github.lambdas.ensimegradle.show._

case class EnsimeProject(id: EnsimeProjectId,
                         depends: Set[EnsimeProjectId],
                         sources: Set[Path],
                         targets: Set[Path],
                         scalacOptions: Seq[String],
                         javacOptions: Seq[String],
                         libraryJars: Set[Path],
                         librarySources: Set[Path],
                         libraryDocs: Set[Path])

object EnsimeProject {
  implicit val ensimeProjectShow: Show[EnsimeProject] = {
    Show.show { p =>
      show"""(:id ${p.id}
            | :depends ${p.depends}
            | :sources ${p.sources}
            | :targets ${p.targets}
            | :scalac-options ${p.scalacOptions}
            | :javac-options ${p.javacOptions}
            | :library-jars ${p.libraryJars}
            | :library-sources ${p.librarySources}
            | :library-docs ${p.libraryDocs}
            |)
          """.stripMargin
    }
  }
}
