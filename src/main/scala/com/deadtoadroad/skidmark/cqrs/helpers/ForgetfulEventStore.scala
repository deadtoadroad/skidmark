package com.deadtoadroad.skidmark.cqrs.helpers

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.EventStore
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

class ForgetfulEventStore() extends EventStore {
  override def load[A <: Aggregate](id: UUID): IO[List[Event[A]]] = IO.pure(Nil)

  override def save[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] = IO.unit
}

object ForgetfulEventStore {
  def apply(): EventStore = new ForgetfulEventStore()
}
