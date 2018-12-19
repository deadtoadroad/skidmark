package com.deadtoadroad.skidmark.model.commands.post

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.UpdateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Post
import com.deadtoadroad.skidmark.model.events.post.PostPublished

case class PublishPost(
  id: UUID,
  version: Int,
  publishedOn: Option[ZonedDateTime]
) extends UpdateCommand[Post] {
  override def execute(post: Post): Either[Throwable, List[UpdateEvent[Post]]] =
    Right(
      List(
        PostPublished(
          id = id,
          version = version + 1,
          publishedOn = publishedOn.getOrElse(ZonedDateTime.now())
        )
      )
    )
}
