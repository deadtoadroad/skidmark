package com.deadtoadroad.skidmark.db.model

import java.time.ZonedDateTime
import java.util.UUID

import scala.language.implicitConversions

case class PostIndexItem(
  id: UUID,
  version: Int,
  title: String,
  author: String,
  tags: List[String],
  publishedOn: Option[ZonedDateTime],
  comments: Int,
  createdOn: ZonedDateTime,
  updatedOn: Option[ZonedDateTime],
  isDeleted: Boolean
)

object PostIndexItem {
  implicit def post2PostIndexItem(post: Post): PostIndexItem =
    PostIndexItem(
      id = post.id,
      version = post.version,
      title = post.title,
      author = post.author,
      tags = post.tags,
      publishedOn = post.publishedOn,
      comments = post.comments.length,
      createdOn = post.createdOn,
      updatedOn = post.updatedOn,
      isDeleted = post.isDeleted
    )
}
