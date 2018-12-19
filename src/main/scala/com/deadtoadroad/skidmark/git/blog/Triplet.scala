package com.deadtoadroad.skidmark.git.blog

import com.deadtoadroad.skidmark.db.model.PublishedPostIndexItem

case class Triplet(
  post: PublishedPostIndexItem,
  previousPost: Option[PublishedPostIndexItem],
  nextPost: Option[PublishedPostIndexItem]
)

object Triplet {
  def apply(posts: List[PublishedPostIndexItem]): List[Triplet] =
    posts.headOption
      .map(_ => posts.map(Some.apply))
      .map(None :: _ ++ List(None))
      .map(_.sliding(3))
      .map(_.map(t => t(1).map(Triplet(_, t(2), t.head))))
      .toList.flatten.flatten
}
