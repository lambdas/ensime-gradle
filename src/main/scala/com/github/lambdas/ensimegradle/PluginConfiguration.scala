package com.github.lambdas.ensimegradle

class PluginConfiguration {

  private var _cacheDir: String = ".ensime_cache"

  def cacheDir: String = _cacheDir

  def getCacheDir: String = _cacheDir

  def setCacheDir(dir: String): Unit = {
    _cacheDir = dir
  }

  private var _scalaVersion: Option[String] = None

  def scalaVersion: Option[String] = _scalaVersion

  def getScalaVersion: String = _scalaVersion.orNull

  def setScalaVersion(version: String): Unit = {
    _scalaVersion = Option(version)
  }
}
