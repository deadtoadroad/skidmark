package com.deadtoadroad.skidmark.text

import scala.util.matching.Regex

// Takes most strings and converts them to lowercase words separated by dashes.
object Slugifier {
  // Case boundaries.
  private val cbR: Regex =
    """(\p{Lower})(\p{Upper})""".r
  private val separate: String => String = replace(cbR, m => s"${m.group(1)}-${m.group(2)}")

  // Punctuation.
  private val pR: Regex =
    """'""".r
  private val depunctuate: String => String = replace(pR, "")

  // Non-word characters.
  private val nwR: Regex =
    """[\W_]+""".r
  private val sanitise: String => String = replace(nwR, "-")

  // Dashes.
  private val d: String =
    """[-]+"""
  private val dR: Regex = d.r
  private val `^dR`: Regex = raw"^$dR".r
  private val `d$R`: Regex = raw"$dR$$".r
  private val trimStart: String => String = replace(`^dR`, "")
  private val trimEnd: String => String = replace(`d$R`, "")
  private val trim: String => String = trimStart andThen trimEnd
  private val deduplicate: String => String = replace(dR, "-")

  // Lower case.
  private val lowerCase: String => String = (string: String) => string.toLowerCase

  def slugify(string: String): String =
    Function.chain(Seq(separate, depunctuate, sanitise, trim, deduplicate, lowerCase))(string)

  private def replace(regex: Regex, replacement: String)(string: String): String =
    regex.replaceAllIn(string, replacement)

  private def replace(regex: Regex, replacer: Regex.Match => String)(string: String): String =
    regex.replaceAllIn(string, replacer)
}
