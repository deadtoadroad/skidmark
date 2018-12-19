package com.deadtoadroad.skidmark.db.model

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.model.events.comment.CommentCreated

import scala.language.implicitConversions

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
) extends Entity

object Comment {
  implicit def commentCreated2Comment(commentCreated: CommentCreated): Comment =
    Comment(
      id = commentCreated.id,
      version = commentCreated.version,
      postId = commentCreated.postId,
      author = commentCreated.author,
      text = commentCreated.text,
      vettedOn = None,
      createdOn = commentCreated.createdOn,
      updatedOn = None,
      isDeleted = false
    )
}
