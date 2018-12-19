package com.deadtoadroad.skidmark.cqrs.model.events

import java.time.ZonedDateTime

import com.deadtoadroad.skidmark.cqrs.model.Aggregate

trait CreateEvent[A <: Aggregate] extends Event[A] {
  val createdOn: ZonedDateTime

  def create(): Either[Throwable, A]
}
