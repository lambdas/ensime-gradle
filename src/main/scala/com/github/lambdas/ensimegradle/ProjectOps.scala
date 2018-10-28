package com.github.lambdas.ensimegradle

import java.nio.file.Path

import org.gradle.api.Project
import org.gradle.api.component.Artifact
import org.gradle.api.internal.artifacts.result.DefaultResolvedArtifactResult
import org.gradle.api.internal.tasks.compile.{DefaultJavaCompileSpecFactory, JavaCompilerArgumentsBuilder}
import org.gradle.api.internal.tasks.scala.{DefaultScalaJavaJointCompileSpec, ZincScalaCompilerArgumentsGenerator}
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.scala.ScalaCompile
import org.gradle.jvm.JvmLibrary
import org.gradle.language.base.artifact.SourcesArtifact
import org.gradle.language.java.artifact.JavadocArtifact

import scala.collection.JavaConverters._

class ProjectOps(private val u: Project) extends AnyVal {
  def dependencyArtifacts(specifier: Class[_ <: Artifact]): Set[Path] = {
    val componentIds = u.getConfigurations
      .getByName("compile")
      .getIncoming
      .getResolutionResult
      .getAllComponents.asScala
      .map(_.getId)

    def result = u.getDependencies.createArtifactResolutionQuery
      .forComponents(componentIds.asJava)
      .withArtifacts(classOf[JvmLibrary], specifier)
      .execute()

    result.getResolvedComponents.asScala
      .flatMap { component =>
        component.getArtifacts(specifier).asScala
          .collect { case r: DefaultResolvedArtifactResult => r }
          .map(_.getFile.toPath)
      }
      .toSet
  }

  def ensimeServerJars(scalaVersionMajor: String, ensimeServerVersion: String): Set[Path] = {
    u.getConfigurations.create("ensime-server")
    u.getDependencies.add("ensime-server", s"org.ensime:server_$scalaVersionMajor:$ensimeServerVersion")
    u.getConfigurations.getByName("ensime-server").resolve().asScala.map(_.toPath).toSet
  }

  def dependencyJars: Set[Path] = {
    u.getConfigurations.getByName("compile").resolve().asScala.map(_.toPath).toSet
  }

  def dependencySources: Set[Path] = dependencyArtifacts(classOf[SourcesArtifact])

  def dependencyJavadoc: Set[Path] = dependencyArtifacts(classOf[JavadocArtifact])

  def scalaCompilerJars: Set[Path] = {
    compileScalaTask.getScalaClasspath.getFiles.asScala.map(_.toPath).toSet
  }

  def scalaVersionFromScalaLibrary: Option[String] = {
    u.getConfigurations.getByName("compile").getDependencies.asScala
      .find { d => d.getGroup == "org.scala-lang" && d.getName == "scala-library" }
      .map(_.getVersion)
  }

  def compileScalaTask: ScalaCompile = {
    u.getTasksByName("compileScala", false).asScala
      .collect { case o: ScalaCompile => o }
      .head
  }

  def compileJavaTask: JavaCompile = {
    u.getTasksByName("compileJava", false).asScala
      .collect { case o: JavaCompile => o }
      .head
  }

  def sources: Set[Path] = {
    u.getExtensions.getByName("sourceSets").asInstanceOf[SourceSetContainer].asScala
      .flatMap(_.getAllSource.getSrcDirs.asScala)
      .map(_.toPath)
      .toSet
  }

  def targets: Set[Path] = {
    Set(u.getBuildDir.toPath.resolve("classes"))
  }

  def scalacOptions: Seq[String] = {
    val opts = compileScalaTask.getScalaCompileOptions
    val spec = new DefaultScalaJavaJointCompileSpec()
    spec.setScalaCompileOptions(opts)
    new ZincScalaCompilerArgumentsGenerator().generate(spec).asScala
  }

  def javacOptions: Seq[String] = {
    val opts = compileJavaTask.getOptions
    val spec = new DefaultJavaCompileSpecFactory(opts).create
    spec.setCompileOptions(opts)
    val builder = new JavaCompilerArgumentsBuilder(spec)
      .includeClasspath(false)
      .includeLauncherOptions(false)
      .includeMainOptions(true)
      .includeSourceFiles(false)
    builder.build().asScala
  }
}

object ProjectOps {
  implicit def toProjectOps(u: Project): ProjectOps = new ProjectOps(u)
}

