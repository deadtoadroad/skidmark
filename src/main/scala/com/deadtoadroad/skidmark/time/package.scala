package com.deadtoadroad.skidmark

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import scala.language.implicitConversions

package object time {
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  implicit val zonedDateTimeOrdering: Ordering[ZonedDateTime] = _ compareTo _
}
