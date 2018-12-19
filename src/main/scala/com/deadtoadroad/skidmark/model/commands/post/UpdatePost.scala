package com.deadtoadroad.skidmark.model.commands.post

import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.commands.UpdateCommand
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.model.Post
import com.deadtoadroad.skidmark.model.events.post.{PostAuthorUpdated, PostTagsUpdated, PostTextUpdated, PostTitleUpdated}

case class UpdatePost(
  id: UUID,
  version: Int,
  title: Option[String],
  author: Option[String],
  tags: Option[List[String]],
  text: Option[String]
) extends UpdateCommand[Post] {
  override def execute(post: Post): Either[Throwable, List[UpdateEvent[Post]]] =
    Right(
      List(
        title.map(t => (i: Int) => PostTitleUpdated(id, version + 1 + i, t)),
        author.map(a => (i: Int) => PostAuthorUpdated(id, version + 1 + i, a)),
        tags.map(t => (i: Int) => PostTagsUpdated(id, version + 1 + i, t)),
        text.map(t => (i: Int) => PostTextUpdated(id, version + 1 + i, t))
      )
        .flatten.zipWithIndex.map(e => e._1(e._2))
    )
}
