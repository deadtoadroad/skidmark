package com.deadtoadroad.skidmark.git.blog

import scala.language.implicitConversions

class StringBuilderOps(stringBuilder: StringBuilder) {
  def appendLine(): StringBuilder =
    stringBuilder
      .append(System.lineSeparator())

  def appendLine(s: String): StringBuilder =
    stringBuilder
      .append(s)
      .append(System.lineSeparator())

  def indent(i: Int): StringBuilder =
    stringBuilder
      .append(" " * i)
}

object StringBuilderOps {
  implicit def stringBuilder2StringBuilderOps(stringBuilder: StringBuilder): StringBuilderOps =
    new StringBuilderOps(stringBuilder)
}
