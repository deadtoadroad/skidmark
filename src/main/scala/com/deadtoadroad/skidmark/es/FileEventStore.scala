package com.deadtoadroad.skidmark.es

import java.nio.file.NoSuchFileException
import java.util.UUID

import cats.effect.IO
import cats.implicits._
import com.deadtoadroad.skidmark.cqrs.EventStore
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.es.serialisers.EventSerialiser
import com.deadtoadroad.skidmark.io.File

class FileEventStore(root: String) extends EventStore {
  private val eventSerialiser: EventSerialiser = EventSerialiser()

  override def load[A <: Aggregate](id: UUID): IO[List[Event[A]]] =
    aggregateFile(id).readLines()
      .handleError { case _: NoSuchFileException => Nil }
      .flatMap(_.map(eventSerialiser.deserialise[A]).sequence[IO, Event[A]])

  override def save[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] =
    for {
      lines <- events.map(eventSerialiser.serialise).sequence[IO, String]
      _ <- aggregateFile(id).appendLines(lines)
      _ <- allFile(id).appendLines(lines)
    } yield Unit

  private def aggregateFile(id: UUID): File = {
    val directories = s"$id".take(8).sliding(2, 2).mkString("/")
    File(s"$root/$directories/$id")
  }

  private def allFile(id: UUID): File = File(s"$root/_all")
}

object FileEventStore {
  def apply(root: String): EventStore = new FileEventStore(root)
}
