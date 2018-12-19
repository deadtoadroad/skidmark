package com.deadtoadroad.skidmark.cqrs.model.commands

import java.util.UUID

trait OptionalId {
  val id: Option[UUID]
}
