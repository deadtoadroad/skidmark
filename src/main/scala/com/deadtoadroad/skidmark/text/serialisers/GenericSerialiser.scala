package com.deadtoadroad.skidmark.text.serialisers

import cats.effect.IO
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.json4s.{Formats, ShortTypeHints}

class GenericSerialiser(types: List[Class[_]]) {
  private implicit val formats: Formats = Serialization.formats(ShortTypeHints(types)) +
    UUIDSerialiser() +
    ZonedDateTimeSerialiser()

  def deserialise[T <: AnyRef](string: String): IO[T] = IO(read[AnyRef](string)).map(_.asInstanceOf[T])

  def serialise[T <: AnyRef](anyRef: T): IO[String] = IO(write(anyRef))
}

object GenericSerialiser {
  def apply(types: List[Class[_]]): GenericSerialiser = new GenericSerialiser(types)
}
