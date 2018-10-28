package com.github.lambdas.ensimegradle

import java.nio.file.Path

import cats.Show
import cats.syntax.show._
import com.github.lambdas.ensimegradle.show._

case class EnsimeConfig(rootDir: Path,
                        cacheDir: Path,
                        javaHome: Path,
                        name: String,
                        scalaVersion: String,
                        javaSources: Set[Path],
                        projects: Set[EnsimeProject],
                        ensimeServerJars: Set[Path],
                        scalaCompilerJars: Set[Path])

object EnsimeConfig {
  implicit val ensimeConfigShow: Show[EnsimeConfig] = {
    Show.show { c =>
      show"""(:root-dir ${c.rootDir}
            | :cache-dir ${c.cacheDir}
            | :java-home ${c.javaHome}
            | :name ${c.name}
            | :scala-version ${c.scalaVersion}
            | :java-sources ${c.javaSources}
            | :projects ${c.projects}
            | :ensime-server-jars ${c.ensimeServerJars}
            | :scala-compiler-jars ${c.scalaCompilerJars}
            | :java-flags ()
            | :shut-down-on-disconnect "true"
            |)
        """.stripMargin
    }
  }
}