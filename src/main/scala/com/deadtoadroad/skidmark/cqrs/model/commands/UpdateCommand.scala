package com.deadtoadroad.skidmark.cqrs.model.commands

import cats.implicits._
import com.deadtoadroad.skidmark.cqrs.model.events.UpdateEvent
import com.deadtoadroad.skidmark.cqrs.model.{Aggregate, Id, Version}

trait UpdateCommand[A <: Aggregate] extends Command[A] with Id with Version {
  def validate(aggregate: A): Either[Throwable, Unit] =
    Right[Throwable, Unit](Unit)
      .ensure(new Exception("Command id does not match aggregate id."))(_ => id == aggregate.id)
      .ensure(new Exception("Command version does not match aggregate version."))(_ => version == aggregate.version)

  def execute(aggregate: A): Either[Throwable, List[UpdateEvent[A]]]
}
