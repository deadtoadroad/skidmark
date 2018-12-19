package com.deadtoadroad.skidmark.model.events.comment

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.events.CreateEvent
import com.deadtoadroad.skidmark.model.Comment

case class CommentCreated(
  id: UUID,
  version: Int = 0,
  postId: UUID,
  author: String,
  text: String,
  createdOn: ZonedDateTime = ZonedDateTime.now()
) extends CreateEvent[Comment] {
  override def create(): Either[Throwable, Comment] =
    Right(
      Comment(
        id = id,
        version = version,
        postId = postId,
        author = author,
        text = text,
        vettedOn = None,
        createdOn = createdOn,
        updatedOn = None,
        isDeleted = false
      )
    )
}
