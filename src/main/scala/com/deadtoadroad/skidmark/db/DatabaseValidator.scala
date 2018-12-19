package com.deadtoadroad.skidmark.db

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.Validator
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event

class DatabaseValidator(database: Database) extends Validator {
  // Check post exists, is published, and isn't deleted when creating/updating comments.
  override def validate[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] = IO.unit
}

object DatabaseValidator {
  def apply(database: Database): Validator = new DatabaseValidator(database)
}
