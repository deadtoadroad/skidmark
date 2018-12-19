package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

trait EventStore {
  def load[A <: Aggregate](id: UUID): IO[List[Event[A]]]

  def save[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit]
}
