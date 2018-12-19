package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

// Commands take care of validation within an aggregate.
// This trait is used for validation between aggregates.
trait Validator {
  def validate[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit]
}
