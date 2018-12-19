package com.deadtoadroad.skidmark.text.serialisers

import java.util.UUID

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

class UUIDSerialiser() extends CustomSerializer[UUID](_ => ( {
  case JString(uuid) => UUID.fromString(uuid)
}, {
  case uuid: UUID => JString(uuid.toString)
}))

object UUIDSerialiser {
  def apply(): UUIDSerialiser = new UUIDSerialiser()
}
