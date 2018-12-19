package com.deadtoadroad.skidmark.model.commands.post

import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.CreateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.CreateEvent
import com.deadtoadroad.skidmark.model.Post
import com.deadtoadroad.skidmark.model.events.post.PostCreated

case class CreatePost(
  id: Option[UUID],
  title: String,
  author: String,
  tags: Option[List[String]],
  text: Option[String]
) extends CreateCommand[Post] {
  override def execute(): Either[Throwable, CreateEvent[Post]] =
    Right(
      PostCreated(
        id = id.getOrElse(UUID.randomUUID()),
        title = title,
        author = author,
        tags = tags,
        text = text
      )
    )
}
