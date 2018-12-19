package com.deadtoadroad.skidmark.db

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.{Event, UpdateEvent}
import com.deadtoadroad.skidmark.cqrs.{Publisher, Subscriber}
import com.deadtoadroad.skidmark.db.model.{Comment, Post}
import com.deadtoadroad.skidmark.model.events.comment._
import com.deadtoadroad.skidmark.model.events.post._

class DatabaseSubscriber(database: Database, republisher: Publisher) extends Subscriber {
  override def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
    updateDatabase(id, event)
      .flatMap(_ => republisher.publish(id, event))

  private def updateDatabase[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
    event match {
      case e: PostCreated => database.createPost(e)
      case e: PostTitleUpdated => updatePost(id, e, _.copy(title = e.title))
      case e: PostAuthorUpdated => updatePost(id, e, _.copy(author = e.author))
      case e: PostTagsUpdated => updatePost(id, e, _.copy(tags = e.tags))
      case e: PostTextUpdated => updatePost(id, e, _.copy(text = e.text))
      case e: PostPublished => updatePost(id, e, _.copy(publishedOn = Some(e.publishedOn)))
      case e: PostUnpublished => updatePost(id, e, _.copy(publishedOn = None))
      case e: CommentCreated => database.createComment(e)
      case e: CommentAuthorUpdated => updateComment(id, e, _.copy(author = e.author))
      case e: CommentTextUpdated => updateComment(id, e, _.copy(text = e.text))
      case e: CommentVetted => updateComment(id, e, _.copy(vettedOn = Some(e.vettedOn)))
      case e: CommentUnvetted => updateComment(id, e, _.copy(vettedOn = None))
    }

  private def updatePost(id: UUID, event: UpdateEvent[_], action: Post => Post): IO[Unit] =
    database.getPost(id)
      .map(post => post.copy(
        version = event.version,
        updatedOn = Some(event.updatedOn)
      ))
      .map(action)
      .flatMap(database.updatePost)

  private def updateComment(id: UUID, event: UpdateEvent[_], action: Comment => Comment): IO[Unit] =
    database.getComment(id)
      .map(comment => comment.copy(
        version = event.version,
        updatedOn = Some(event.updatedOn)
      ))
      .map(action)
      .flatMap(database.updateComment)
}

object DatabaseSubscriber {
  def apply(database: Database, republisher: Publisher): Subscriber = new DatabaseSubscriber(database, republisher)
}
