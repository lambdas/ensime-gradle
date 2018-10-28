package com.github.lambdas.ensimegradle

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}

import cats.syntax.show._
import com.github.lambdas.ensimegradle.ProjectOps._
import org.gradle.api.{Plugin, Project}

import scala.collection.JavaConverters._

class EnsimeGradlePlugin extends Plugin[Project] {

  override def apply(project: Project): Unit = {
    val pluginConfig = project.getExtensions.create("ensime", classOf[PluginConfiguration])

    project.task("ensime").doLast { _ =>
      val writer = new PrintWriter(new File(".ensime"))
      writer.print(ensimeConfiguration(project, pluginConfig).show)
      writer.close()
    }
  }

  private def ensimeConfiguration(implicit project: Project, pluginConfig: PluginConfiguration): EnsimeConfig = {
    EnsimeConfig(
      rootDir = project.getRootDir.toPath,
      cacheDir = project.getRootDir.toPath.resolve(pluginConfig.cacheDir),
      javaHome = javaHome,
      name = project.getName,
      scalaVersion = scalaVersion(project, pluginConfig),
      javaSources = Set(javaHome.resolve("src.zip")),
      projects = projects,
      ensimeServerJars = project.ensimeServerJars(scalaVersionMajor(project, pluginConfig), "2.0.2") + javaHome.resolve("lib").resolve("tools.jar"),
      scalaCompilerJars = project.scalaCompilerJars)
  }

  private def scalaVersionMajor(project: Project, pluginConfig: PluginConfiguration): String = {
    val majorPattern = "(\\d+\\.\\d+).*".r
    val majorPattern(v) = scalaVersion(project, pluginConfig)
    v
  }

  private def scalaVersion(project: Project, pluginConfig: PluginConfiguration): String = {
    pluginConfig.scalaVersion
      .orElse(project.scalaVersionFromScalaLibrary)
      .getOrElse(throw new IllegalArgumentException("Can't infer scala version"))
  }

  private def javaHome: Path = {
    val path = Paths.get(System.getProperty("java.home"))
    if (path.endsWith("jre")) path.getParent else path
  }

  private def ensimeProject(project: Project): EnsimeProject = {
    EnsimeProject(
      id = ensimeProjectId(project),
      depends = project.getSubprojects.asScala.map(ensimeProjectId).toSet,
      sources = project.sources,
      targets = project.targets,
      scalacOptions = project.scalacOptions,
      javacOptions = project.javacOptions,
      libraryJars = project.dependencyJars,
      librarySources = project.dependencySources,
      libraryDocs = project.dependencyJavadoc)
  }

  private def ensimeProjectId(project: Project): EnsimeProjectId = {
    EnsimeProjectId(project.getName, "compile")
  }

  private def projects(implicit project: Project): Set[EnsimeProject] = {
    (project.getSubprojects.asScala.toSet + project).map(ensimeProject)
  }
}

