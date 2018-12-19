package com.deadtoadroad.skidmark.model.events.comment

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Comment

case class CommentVetted(
  id: UUID,
  version: Int,
  vettedOn: ZonedDateTime,
  updatedOn: ZonedDateTime = ZonedDateTime.now()
) extends UpdateEvent[Comment] {
  override def update(comment: Comment): Either[Throwable, Comment] =
    Right(
      comment.copy(
        id = id,
        version = version,
        vettedOn = Some(vettedOn),
        updatedOn = Some(updatedOn)
      )
    )
}
