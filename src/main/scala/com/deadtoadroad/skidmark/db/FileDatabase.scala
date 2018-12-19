package com.deadtoadroad.skidmark.db

import java.nio.file.NoSuchFileException
import java.time.ZonedDateTime
import java.util.UUID

import cats.effect.IO
import cats.implicits._
import com.deadtoadroad.skidmark.db.model._
import com.deadtoadroad.skidmark.db.serialisers.Serialiser
import com.deadtoadroad.skidmark.io.File
import com.deadtoadroad.skidmark.reflect.Utilities.getRuntimeClassFromClassTag
import com.deadtoadroad.skidmark.text.Slugifier.slugify
import com.deadtoadroad.skidmark.time._

import scala.reflect.ClassTag

class FileDatabase(val root: String) extends Database {

  private val serialiser: Serialiser = Serialiser()

  override def getPost(id: UUID): IO[Post] =
    readEntity[Post](id)

  override def getPosts: IO[List[PostIndexItem]] =
    getPostIndex
      .map(_.items)

  override def getPublishedPosts: IO[List[PublishedPostIndexItem]] =
    getPosts
      .map(_.map(PublishedPostIndexItem.apply).flatten)

  override def createPost(post: Post): IO[Unit] =
    writeEntity(post)
      .flatMap(_ => updatePostIndex(post))

  override def updatePost(post: Post): IO[Unit] =
    writeEntity(post)
      .flatMap(_ => updatePostIndex(post))

  override def getComment(id: UUID): IO[Comment] =
    readEntity[Comment](id)

  override def createComment(comment: Comment): IO[Unit] =
    writeEntity(comment)
      .flatMap(_ => updateCommentIndex(comment))
      .flatMap(_ => getPost(comment.postId))
      .map(post => post.copy(comments = post.comments
        .:+(comment)
        .sortBy(_.createdOn)
      ))
      .flatMap(updatePost)

  override def updateComment(comment: Comment): IO[Unit] =
    writeEntity(comment)
      .flatMap(_ => updateCommentIndex(comment))
      .flatMap(_ => getPost(comment.postId))
      .map(post => post.copy(comments = post.comments
        .filter(_.id != comment.id)
        .::(comment)
        .sortBy(_.createdOn)
      ))
      .flatMap(updatePost)

  private def readEntity[E <: Entity : ClassTag](id: UUID): IO[E] =
    entityFile(id).readText()
      .flatMap(serialiser.deserialise)

  private def writeEntity[E <: Entity : ClassTag](entity: E): IO[Unit] =
    serialiser.serialise(entity)
      .flatMap(entityFile(entity.id).writeText)

  private def entityFile[E <: Entity : ClassTag](id: UUID): File =
    File(entityPath(id))

  private def entityPath[E <: Entity : ClassTag](id: UUID): String = {
    val directories = s"$id".take(8).sliding(2, 2).mkString("/")
    s"$root/${slugify(getRuntimeClassFromClassTag[E].getSimpleName)}/$directories/$id"
  }

  private def getPostIndex: IO[PostIndex] =
    readIndex[PostIndex](PostIndex.name, PostIndex())

  private def updatePostIndex(postIndexItem: PostIndexItem): IO[Unit] =
    getPostIndex
      .map(index => index.copy(items = index.items
        .filter(i => i.id != postIndexItem.id)
        .::(postIndexItem)
        .sortBy(i => i.updatedOn.getOrElse(i.createdOn))(Ordering[ZonedDateTime].reverse)
        .sortBy(_.publishedOn)(Ordering[Option[ZonedDateTime]].reverse)
      ))
      .flatMap(writeIndex(PostIndex.name, _))

  private def getCommentIndex: IO[CommentIndex] =
    readIndex[CommentIndex](CommentIndex.name, CommentIndex())

  private def updateCommentIndex(commentIndexItem: CommentIndexItem): IO[Unit] =
    getCommentIndex
      .map(index => index.copy(items = index.items
        .filter(i => i.id != commentIndexItem.id)
        .::(commentIndexItem)
        .sortBy(_.createdOn)(Ordering[ZonedDateTime].reverse)
      ))
      .flatMap(writeIndex(CommentIndex.name, _))

  private def readIndex[I <: AnyRef](name: String, default: => I): IO[I] =
    indexFile(name).readText()
      .flatMap(serialiser.deserialise[I])
      .handleError { case _: NoSuchFileException => default }

  private def writeIndex[I <: AnyRef](name: String, items: I): IO[Unit] =
    serialiser.serialise(items)
      .flatMap(indexFile(name).writeText)

  private def indexFile(name: String): File =
    File(indexPath(name))

  private def indexPath(name: String): String =
    s"$root/$name"
}

object FileDatabase {
  def apply(root: String): Database = new FileDatabase(root)
}
