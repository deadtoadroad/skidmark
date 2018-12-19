package com.deadtoadroad.skidmark.blog

import cats.effect.IO
import com.deadtoadroad.skidmark.db.model.Post

trait Blog {
  def writePost(post: Post): IO[Unit]

  def writePosts(): IO[Unit]

  def writePostSummary(): IO[Unit]

  def writeTagSummary(): IO[Unit]

  def deletePosts(): IO[Unit]
}
