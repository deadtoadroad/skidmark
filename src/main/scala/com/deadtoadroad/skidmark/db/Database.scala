package com.deadtoadroad.skidmark.db

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.db.model.{Comment, Post, PostIndexItem, PublishedPostIndexItem}

trait Database {
  def getPost(id: UUID): IO[Post]

  def getPosts: IO[List[PostIndexItem]]

  def getPublishedPosts: IO[List[PublishedPostIndexItem]]

  def createPost(post: Post): IO[Unit]

  def updatePost(post: Post): IO[Unit]

  def getComment(id: UUID): IO[Comment]

  def createComment(comment: Comment): IO[Unit]

  def updateComment(comment: Comment): IO[Unit]
}
