package com.deadtoadroad.skidmark.es.serialisers

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.model.events.comment._
import com.deadtoadroad.skidmark.model.events.post._
import com.deadtoadroad.skidmark.text.serialisers.GenericSerialiser

class EventSerialiser() {
  private val genericSerialiser: GenericSerialiser =
    GenericSerialiser(List(
      classOf[PostCreated],
      classOf[PostTitleUpdated],
      classOf[PostAuthorUpdated],
      classOf[PostTagsUpdated],
      classOf[PostTextUpdated],
      classOf[PostPublished],
      classOf[PostUnpublished],
      classOf[CommentCreated],
      classOf[CommentAuthorUpdated],
      classOf[CommentTextUpdated],
      classOf[CommentVetted],
      classOf[CommentUnvetted]
    ))

  def deserialise[A <: Aggregate](string: String): IO[Event[A]] = genericSerialiser.deserialise[Event[A]](string)

  def serialise[A <: Aggregate](event: Event[A]): IO[String] = genericSerialiser.serialise(event)
}

object EventSerialiser {
  def apply(): EventSerialiser = new EventSerialiser()
}
