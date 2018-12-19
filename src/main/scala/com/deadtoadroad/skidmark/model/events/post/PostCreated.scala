package com.deadtoadroad.skidmark.model.events.post

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.events.CreateEvent
import com.deadtoadroad.skidmark.model.Post

case class PostCreated(
  id: UUID,
  version: Int = 0,
  title: String,
  author: String,
  tags: Option[List[String]],
  text: Option[String],
  createdOn: ZonedDateTime = ZonedDateTime.now()
) extends CreateEvent[Post] {
  override def create(): Either[Throwable, Post] =
    Right(
      Post(
        id = id,
        version = version,
        title = title,
        author = author,
        tags = tags.getOrElse(Nil),
        text = text.getOrElse(""),
        publishedOn = None,
        createdOn = createdOn,
        updatedOn = None,
        isDeleted = false
      )
    )
}
