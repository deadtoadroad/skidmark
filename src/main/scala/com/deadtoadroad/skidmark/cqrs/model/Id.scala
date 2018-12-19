package com.deadtoadroad.skidmark.cqrs.model

import java.util.UUID

trait Id {
  val id: UUID
}

object Id {
  val default: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
}
