package com.deadtoadroad.skidmark.cqrs.model

trait Version {
  val version: Int
}

object Version {
  val default: Int = -1
}
