package com.deadtoadroad.skidmark.db.model

import java.time.ZonedDateTime
import java.util.UUID

trait Entity {
  val id: UUID
  val version: Int
  val createdOn: ZonedDateTime
  val updatedOn: Option[ZonedDateTime]
  val isDeleted: Boolean
}
