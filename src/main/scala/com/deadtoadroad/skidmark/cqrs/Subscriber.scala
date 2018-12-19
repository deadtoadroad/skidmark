package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

trait Subscriber {
  def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit]
}
