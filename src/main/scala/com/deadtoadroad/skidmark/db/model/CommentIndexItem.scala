package com.deadtoadroad.skidmark.db.model

import java.time.ZonedDateTime
import java.util.UUID

import scala.language.implicitConversions

case class CommentIndexItem(
  id: UUID,
  version: Int,
  postId: UUID,
  author: String,
  vettedOn: Option[ZonedDateTime],
  createdOn: ZonedDateTime,
  updatedOn: Option[ZonedDateTime],
  isDeleted: Boolean
)

object CommentIndexItem {
  implicit def comment2CommentIndexItem(comment: Comment): CommentIndexItem =
    CommentIndexItem(
      id = comment.id,
      version = comment.version,
      postId = comment.postId,
      author = comment.author,
      vettedOn = comment.vettedOn,
      createdOn = comment.createdOn,
      updatedOn = comment.updatedOn,
      isDeleted = comment.isDeleted
    )
}
