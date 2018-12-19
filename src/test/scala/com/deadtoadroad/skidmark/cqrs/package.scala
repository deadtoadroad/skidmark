package com.deadtoadroad.skidmark

import java.time.ZonedDateTime
import java.util.UUID

import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.commands.{CreateCommand, UpdateCommand}
import com.deadtoadroad.skidmark.cqrs.model.events.{CreateEvent, UpdateEvent}

package object cqrs {

  case class Thing(
    id: UUID,
    version: Int,
    name: String,
    createdOn: ZonedDateTime,
    updatedOn: Option[ZonedDateTime],
    isDeleted: Boolean
  ) extends Aggregate

  case class CreateThing(
    id: Option[UUID] = None,
    name: Option[String] = None
  ) extends CreateCommand[Thing] {
    override def execute(): Either[Throwable, CreateEvent[Thing]] =
      Right(
        ThingCreated(
          id = id.getOrElse(UUID.randomUUID()),
          name = name
        )
      )
  }

  case class ThingCreated(
    id: UUID,
    version: Int = 0,
    name: Option[String],
    createdOn: ZonedDateTime = ZonedDateTime.now()
  ) extends CreateEvent[Thing] {
    override def create(): Either[Throwable, Thing] =
      Right(
        Thing(
          id = id,
          version = version,
          name = name.getOrElse(""),
          createdOn = createdOn,
          updatedOn = None,
          isDeleted = false
        )
      )
  }

  case class UpdateThing(
    id: UUID,
    version: Int,
    name: Option[String] = None
  ) extends UpdateCommand[Thing] {
    override def execute(thing: Thing): Either[Throwable, List[UpdateEvent[Thing]]] =
      Right(List(
        ThingUpdated(
          id = id,
          version = version + 1,
          name = name
        )
      ))
  }

  case class ThingUpdated(
    id: UUID,
    version: Int,
    name: Option[String],
    updatedOn: ZonedDateTime = ZonedDateTime.now()
  ) extends UpdateEvent[Thing] {
    override def update(thing: Thing): Either[Throwable, Thing] =
      Right(
        thing.copy(
          version = version,
          name = name.getOrElse(thing.name),
          updatedOn = Some(updatedOn)
        )
      )
  }

}
