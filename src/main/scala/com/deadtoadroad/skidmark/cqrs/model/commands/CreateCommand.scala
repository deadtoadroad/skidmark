package com.deadtoadroad.skidmark.cqrs.model.commands

import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.CreateEvent

trait CreateCommand[A <: Aggregate] extends Command[A] with OptionalId {
  def execute(): Either[Throwable, CreateEvent[A]]
}
