package com.deadtoadroad.skidmark.model.commands.comment

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.UpdateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Comment
import com.deadtoadroad.skidmark.model.events.comment.CommentVetted

case class VetComment(
  id: UUID,
  version: Int,
  vettedOn: Option[ZonedDateTime]
) extends UpdateCommand[Comment] {
  override def execute(comment: Comment): Either[Throwable, List[UpdateEvent[Comment]]] =
    Right(
      List(
        CommentVetted(
          id = id,
          version = version + 1,
          vettedOn = vettedOn.getOrElse(ZonedDateTime.now())
        )
      )
    )
}
