package com.deadtoadroad.skidmark.model.events.post

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Post

case class PostPublished(
  id: UUID,
  version: Int,
  publishedOn: ZonedDateTime,
  updatedOn: ZonedDateTime = ZonedDateTime.now()
) extends UpdateEvent[Post] {
  override def update(post: Post): Either[Throwable, Post] =
    Right(
      post.copy(
        id = id,
        version = version,
        publishedOn = Some(publishedOn),
        updatedOn = Some(updatedOn)
      )
    )
}
