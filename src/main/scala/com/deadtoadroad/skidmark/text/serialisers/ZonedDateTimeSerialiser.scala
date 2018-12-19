package com.deadtoadroad.skidmark.text.serialisers

import java.time.ZonedDateTime

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

class ZonedDateTimeSerialiser() extends CustomSerializer[ZonedDateTime](_ => ( {
  case JString(zdt) => ZonedDateTime.parse(zdt)
}, {
  case zdt: ZonedDateTime => JString(zdt.toString)
}))

object ZonedDateTimeSerialiser {
  def apply(): ZonedDateTimeSerialiser = new ZonedDateTimeSerialiser()
}
