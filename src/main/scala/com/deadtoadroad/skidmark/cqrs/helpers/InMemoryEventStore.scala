package com.deadtoadroad.skidmark.cqrs.helpers

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.EventStore
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

import scala.collection.mutable

class InMemoryEventStore() extends EventStore {
  private val eventStore = mutable.HashMap[UUID, List[Event[_]]]().withDefaultValue(Nil)

  override def load[A <: Aggregate](id: UUID): IO[List[Event[A]]] =
    IO(eventStore(id).map(_.asInstanceOf[Event[A]]))

  override def save[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] =
    IO(eventStore(id) ++= events)
}

object InMemoryEventStore {
  def apply(): EventStore = new InMemoryEventStore()
}
