package com.deadtoadroad.skidmark.git.blog

import com.deadtoadroad.skidmark.db.model.PublishedPostIndexItem
import com.deadtoadroad.skidmark.text.Slugifier.slugify

import scala.language.implicitConversions

class PublishedPostIndexItemOps(post: PublishedPostIndexItem) {
  private val path: String = s"posts/${post.publishedOn.format(linkDateFormatter)}/${slugify(post.title)}"

  val urlPath: String = s"/$path/readme.md#readme"

  def filePath(fileRoot: String): String = s"$fileRoot/$path/readme.md"
}

object PublishedPostIndexItemOps {
  implicit def publishedPostIndexItem2PublishedPostIndexItemOps(post: PublishedPostIndexItem): PublishedPostIndexItemOps =
    new PublishedPostIndexItemOps(post)
}
