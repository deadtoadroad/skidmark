package com.deadtoadroad.skidmark.model.commands.comment

import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.UpdateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Comment
import com.deadtoadroad.skidmark.model.events.comment.CommentUnvetted

case class UnvetComment(
  id: UUID,
  version: Int
) extends UpdateCommand[Comment] {
  override def execute(comment: Comment): Either[Throwable, List[UpdateEvent[Comment]]] =
    Right(
      List(
        CommentUnvetted(
          id = id,
          version = version + 1
        )
      )
    )
}
