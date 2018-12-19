package com.deadtoadroad.skidmark.db.model

case class PostIndex(
  items: List[PostIndexItem]
)

object PostIndex {
  val name: String = "post/_index"

  def apply(): PostIndex = new PostIndex(Nil)
}
