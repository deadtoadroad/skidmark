package com.deadtoadroad.skidmark.cqrs.model.events

import java.time.ZonedDateTime

import cats.implicits._
import com.deadtoadroad.skidmark.cqrs.model.Aggregate

trait UpdateEvent[A <: Aggregate] extends Event[A] {
  val updatedOn: ZonedDateTime

  def validate(aggregate: A): Either[Throwable, Unit] =
    Right[Throwable, Unit](Unit)
      .ensure(new Exception("Event id does not match aggregate id."))(_ => id == aggregate.id)
      .ensure(new Exception("Event version does not match aggregate version."))(_ => version == aggregate.version + 1)

  def update(aggregate: A): Either[Throwable, A]
}
