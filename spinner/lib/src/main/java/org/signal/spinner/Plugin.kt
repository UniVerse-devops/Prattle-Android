package org.sparkspinner

interface Plugin {
  fun get(): PluginResult
  val name: String
  val path: String
}
