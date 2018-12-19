package com.deadtoadroad.skidmark.cqrs.helpers

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.Validator
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

class HappyValidator() extends Validator {
  override def validate[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] = IO.unit
}

object HappyValidator {
  def apply(): Validator = new HappyValidator()
}
