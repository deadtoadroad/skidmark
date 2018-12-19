package com.deadtoadroad.skidmark.model.commands.post

import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.UpdateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Post
import com.deadtoadroad.skidmark.model.events.post.PostUnpublished

case class UnpublishPost(
  id: UUID,
  version: Int,
) extends UpdateCommand[Post] {
  override def execute(post: Post): Either[Throwable, List[UpdateEvent[Post]]] =
    Right(
      List(
        PostUnpublished(
          id = id,
          version = version + 1
        )
      )
    )
}
