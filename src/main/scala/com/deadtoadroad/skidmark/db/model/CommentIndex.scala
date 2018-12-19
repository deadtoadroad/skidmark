package com.deadtoadroad.skidmark.db.model

case class CommentIndex(
  items: List[CommentIndexItem]
)

object CommentIndex {
  val name: String = "comment/_index"

  def apply(): CommentIndex = new CommentIndex(Nil)
}
