package com.deadtoadroad.skidmark.db.serialisers

import cats.effect.IO
import com.deadtoadroad.skidmark.db.model._
import com.deadtoadroad.skidmark.text.serialisers.GenericSerialiser
import org.json4s.native.JsonMethods.{pretty, render}
import org.json4s.native.JsonParser.parse

class Serialiser() {
  private val genericSerialiser: GenericSerialiser =
    GenericSerialiser(List(
      classOf[Post],
      classOf[PostIndex],
      classOf[PostIndexItem],
      classOf[Comment],
      classOf[CommentIndex],
      classOf[CommentIndexItem]
    ))

  def deserialise[T <: AnyRef](string: String): IO[T] = genericSerialiser.deserialise[T](string)

  def serialise[T <: AnyRef](anyRef: T): IO[String] =
    genericSerialiser.serialise(anyRef)
      .map(parse)
      .map(render)
      .map(pretty)
}

object Serialiser {
  def apply(): Serialiser = new Serialiser()
}
