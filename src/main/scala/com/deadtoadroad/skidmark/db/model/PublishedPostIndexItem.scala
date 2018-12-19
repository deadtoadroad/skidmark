package com.deadtoadroad.skidmark.db.model

import java.time.ZonedDateTime
import java.util.UUID

import scala.language.implicitConversions

case class PublishedPostIndexItem(
  id: UUID,
  version: Int,
  title: String,
  author: String,
  tags: List[String],
  publishedOn: ZonedDateTime,
  comments: Int,
  createdOn: ZonedDateTime,
  updatedOn: Option[ZonedDateTime],
  isDeleted: Boolean
)

object PublishedPostIndexItem {
  def apply(post: PostIndexItem): Option[PublishedPostIndexItem] =
    post.publishedOn.map(publishedOn =>
      PublishedPostIndexItem(
        id = post.id,
        version = post.version,
        title = post.title,
        author = post.author,
        tags = post.tags,
        publishedOn = publishedOn,
        comments = post.comments,
        createdOn = post.createdOn,
        updatedOn = post.updatedOn,
        isDeleted = post.isDeleted
      )
    )
}
