package com.deadtoadroad.skidmark.model.commands.comment

import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.CreateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.CreateEvent
import com.deadtoadroad.skidmark.model.Comment
import com.deadtoadroad.skidmark.model.events.comment.CommentCreated

case class CreateComment(
  id: Option[UUID],
  postId: UUID,
  author: String,
  text: String
) extends CreateCommand[Comment] {
  override def execute(): Either[Throwable, CreateEvent[Comment]] =
    Right(
      CommentCreated(
        id = id.getOrElse(UUID.randomUUID()),
        postId = postId,
        author = author,
        text = text
      )
    )
}
