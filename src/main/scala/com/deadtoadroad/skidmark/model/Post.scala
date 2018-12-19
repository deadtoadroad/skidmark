package com.deadtoadroad.skidmark.model

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.Aggregate

case class Post(
  id: UUID,
  version: Int,
  title: String,
  author: String,
  tags: List[String],
  text: String,
  publishedOn: Option[ZonedDateTime],
  createdOn: ZonedDateTime,
  updatedOn: Option[ZonedDateTime],
  isDeleted: Boolean
) extends Aggregate
