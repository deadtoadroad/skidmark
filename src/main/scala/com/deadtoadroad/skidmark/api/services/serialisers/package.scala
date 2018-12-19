package com.deadtoadroad.skidmark.api.services

import cats.effect.IO
import com.deadtoadroad.skidmark.model.commands.comment.{CreateComment, UnvetComment, UpdateComment, VetComment}
import com.deadtoadroad.skidmark.model.commands.post.{CreatePost, PublishPost, UnpublishPost, UpdatePost}
import com.deadtoadroad.skidmark.model.{Comment, Post}
import io.circe.generic.auto._
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

package object serialisers {
  implicit val postDecoder: EntityDecoder[IO, Post] = jsonOf[IO, Post]

  implicit val createPostDecoder: EntityDecoder[IO, CreatePost] = jsonOf[IO, CreatePost]
  implicit val updatePostDecoder: EntityDecoder[IO, UpdatePost] = jsonOf[IO, UpdatePost]
  implicit val publishPostDecoder: EntityDecoder[IO, PublishPost] = jsonOf[IO, PublishPost]
  implicit val unpublishPostDecoder: EntityDecoder[IO, UnpublishPost] = jsonOf[IO, UnpublishPost]

  implicit val commentDecoder: EntityDecoder[IO, Comment] = jsonOf[IO, Comment]

  implicit val createCommentDecoder: EntityDecoder[IO, CreateComment] = jsonOf[IO, CreateComment]
  implicit val updateCommentDecoder: EntityDecoder[IO, UpdateComment] = jsonOf[IO, UpdateComment]
  implicit val vetCommentDecoder: EntityDecoder[IO, VetComment] = jsonOf[IO, VetComment]
  implicit val unvetCommentDecoder: EntityDecoder[IO, UnvetComment] = jsonOf[IO, UnvetComment]
}
