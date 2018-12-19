package com.deadtoadroad.skidmark.model

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.Aggregate

case class Comment(
  id: UUID,
  version: Int,
  postId: UUID,
  author: String,
  text: String,
  vettedOn: Option[ZonedDateTime],
  createdOn: ZonedDateTime,
  updatedOn: Option[ZonedDateTime],
  isDeleted: Boolean
) extends Aggregate
