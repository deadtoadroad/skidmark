package com.deadtoadroad.skidmark.db.model

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.model.events.post.PostCreated

import scala.language.implicitConversions

case class Post(
  id: UUID,
  version: Int,
  title: String,
  author: String,
  tags: List[String],
  text: String,
  publishedOn: Option[ZonedDateTime],
  comments: List[Comment],
  createdOn: ZonedDateTime,
  updatedOn: Option[ZonedDateTime],
  isDeleted: Boolean
) extends Entity

object Post {
  implicit def postCreated2Post(postCreated: PostCreated): Post =
    Post(
      id = postCreated.id,
      version = postCreated.version,
      title = postCreated.title,
      author = postCreated.author,
      tags = postCreated.tags.getOrElse(Nil),
      text = postCreated.text.getOrElse(""),
      publishedOn = None,
      comments = Nil,
      createdOn = postCreated.createdOn,
      updatedOn = None,
      isDeleted = false
    )
}
