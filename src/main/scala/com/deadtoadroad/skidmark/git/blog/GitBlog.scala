package com.deadtoadroad.skidmark.git.blog

import cats.effect.IO
import cats.implicits._
import com.deadtoadroad.skidmark.blog.Blog
import com.deadtoadroad.skidmark.db.Database
import com.deadtoadroad.skidmark.db.model.Post
import com.deadtoadroad.skidmark.git.blog.PublishedPostIndexItemOps._
import com.deadtoadroad.skidmark.io.File

class GitBlog(database: Database, fileRoot: String, urlRoot: String) extends Blog {
  private val homeFilePath: String = s"$fileRoot/readme.md"
  private val homeUrlPath: String = s"$urlRoot/readme.md#readme"

  override def writePost(post: Post): IO[Unit] =
    for {
      posts <- database.getPublishedPosts
      triplets = Triplet(posts)
      _ <- triplets.headOption.find(t => t.post.id == post.id).map(writePost(home = true, _)).sequence[IO, Unit]
      _ <- triplets.find(t => t.post.id == post.id).map(writePost(home = false, _)).sequence[IO, Unit]
    } yield Unit

  override def writePosts(): IO[Unit] =
    for {
      posts <- database.getPublishedPosts
      triplets = Triplet(posts)
      _ <- triplets.headOption.map(writePost(home = true, _)).sequence[IO, Unit]
      _ <- triplets.map(writePost(home = false, _)).sequence[IO, Unit]
    } yield Unit

  override def writePostSummary(): IO[Unit] = IO.unit

  override def writeTagSummary(): IO[Unit] = IO.unit

  override def deletePosts(): IO[Unit] =
    File(s"$fileRoot/posts")
      .deleteDirectory()

  private def writePost(home: Boolean, triplet: Triplet): IO[Unit] = {
    val filePath = if (home) homeFilePath else triplet.post.filePath(fileRoot)
    val homeUrlPathOption = if (home) None else Some(homeUrlPath)
    for {
      post <- database.getPost(triplet.post.id)
      text = PostWriter(homeUrlPathOption, triplet, post)
      _ <- File(filePath).writeText(text)
    } yield Unit
  }
}

object GitBlog {
  def apply(database: Database, fileRoot: String, urlRoot: String): Blog = new GitBlog(database, fileRoot, urlRoot)
}
