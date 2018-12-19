package com.deadtoadroad.skidmark.cqrs.model

import java.time.{ZoneId, ZonedDateTime}

trait Entity extends Id {
  val createdOn: ZonedDateTime
  val updatedOn: Option[ZonedDateTime]
  val isDeleted: Boolean
}

object Entity {
  val defaultCreatedOn: ZonedDateTime = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
}
