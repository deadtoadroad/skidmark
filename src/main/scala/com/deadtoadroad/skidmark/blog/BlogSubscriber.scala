package com.deadtoadroad.skidmark.blog

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.cqrs.{Publisher, Subscriber}
import com.deadtoadroad.skidmark.db.Database
import com.deadtoadroad.skidmark.db.model.{Comment, Post}
import com.deadtoadroad.skidmark.model.events.comment._
import com.deadtoadroad.skidmark.model.events.post._

class BlogSubscriber(database: Database, blog: Blog, republisher: Publisher) extends Subscriber {
  override def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
    updateBlog(id, event)
      .flatMap(_ => republisher.publish(id, event))

  private def updateBlog[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
    event match {
      case _: PostCreated => IO.unit // Posts start as unpublished. Unsubscribe?
      case _: PostTitleUpdated => ifPublished(id)(_ => writeEverything())
      case _: PostAuthorUpdated => ifPublished(id)(writePostAndSummaries)
      case _: PostTagsUpdated => ifPublished(id)(writePostAndSummaries)
      case _: PostTextUpdated => ifPublished(id)(blog.writePost)
      case _: PostPublished => writeEverything()
      case _: PostUnpublished => writeEverything()
      case _: CommentCreated => ifVetted(id)(withPost(_)(ifPublished(_)(writePostAndSummaries)))
      case _: CommentAuthorUpdated => ifVetted(id)(withPost(_)(ifPublished(_)(writePostAndSummaries)))
      case _: CommentTextUpdated => ifVetted(id)(withPost(_)(ifPublished(_)(writePostAndSummaries)))
      case _: CommentVetted => withComment(id)(withPost(_)(ifPublished(_)(writePostAndSummaries)))
      case _: CommentUnvetted => withComment(id)(withPost(_)(ifPublished(_)(writePostAndSummaries)))
    }

  private def withPost(id: UUID)(action: Post => IO[Unit]): IO[Unit] =
    database.getPost(id)
      .flatMap(action)

  private def withPost(comment: Comment)(action: Post => IO[Unit]): IO[Unit] =
    withPost(comment.postId)(action)

  private def withComment(id: UUID)(action: Comment => IO[Unit]): IO[Unit] =
    database.getComment(id)
      .flatMap(action)

  private def ifPublished(id: UUID)(action: Post => IO[Unit]): IO[Unit] =
    withPost(id)(ifPublished(_)(action))

  private def ifPublished(post: Post)(action: Post => IO[Unit]): IO[Unit] =
    if (post.publishedOn.nonEmpty) action(post)
    else IO.unit

  private def ifVetted(id: UUID)(action: Comment => IO[Unit]): IO[Unit] =
    withComment(id)(ifVetted(_)(action))

  private def ifVetted(comment: Comment)(action: Comment => IO[Unit]): IO[Unit] =
    if (comment.vettedOn.nonEmpty) action(comment)
    else IO.unit

  private def writePostAndSummaries(post: Post): IO[Unit] =
    for {
      _ <- blog.writePost(post)
      _ <- writeSummaries()
    } yield Unit

  // Do this for anything that changes information in summaries, like title, author, tags, publishedOn,
  // or comment count changes.
  private def writeSummaries(): IO[Unit] =
    for {
      _ <- blog.writePostSummary()
      _ <- blog.writeTagSummary()
    } yield Unit

  // Do this for anything that might change previous/next post, like title or publishedOn changes.
  private def writeEverything(): IO[Unit] =
    for {
      _ <- blog.deletePosts()
      _ <- blog.writePosts()
      _ <- writeSummaries()
    } yield Unit
}

object BlogSubscriber {
  def apply(database: Database, blog: Blog, republisher: Publisher): Subscriber =
    new BlogSubscriber(database, blog, republisher)
}
