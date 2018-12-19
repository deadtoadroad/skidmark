package com.deadtoadroad.skidmark.model.commands.comment

import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.UpdateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Comment
import com.deadtoadroad.skidmark.model.events.comment.{CommentAuthorUpdated, CommentTextUpdated, CommentUnvetted}

case class UpdateComment(
  id: UUID,
  version: Int,
  author: Option[String],
  text: Option[String]
) extends UpdateCommand[Comment] {
  override def execute(comment: Comment): Either[Throwable, List[UpdateEvent[Comment]]] =
    Right(
      List(
        author.map(a => (i: Int) => CommentAuthorUpdated(id, version + 1 + i, a)),
        // If updating text, unvet the comment.
        (text, comment.vettedOn) match {
          case (Some(_), Some(_)) => Some((i: Int) => CommentUnvetted(id, version + 1 + i))
          case _ => None
        },
        text.map(t => (i: Int) => CommentTextUpdated(id, version + 1 + i, t))
      )
        .flatten.zipWithIndex.map(e => e._1(e._2))
    )
}
