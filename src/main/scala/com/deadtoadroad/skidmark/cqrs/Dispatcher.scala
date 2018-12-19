package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import cats.implicits._
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.commands.{Command, CreateCommand, UpdateCommand}
import com.deadtoadroad.skidmark.cqrs.model.events.{CreateEvent, Event, UpdateEvent}

import scala.reflect.ClassTag

class Dispatcher(eventStore: EventStore, validator: Validator, publisher: Publisher) {
  def dispatch[A <: Aggregate : ClassTag](command: Command[A]): IO[A] =
    for {
      t <- processCommand(command)
      (aggregate, events) = t
      _ <- validator.validate(aggregate.id, events)
      _ <- eventStore.save(aggregate.id, events)
      _ <- publisher.publish(aggregate.id, events)
    } yield aggregate

  private def processCommand[A <: Aggregate](command: Command[A]): IO[(A, List[Event[A]])] =
    command match {
      case c: CreateCommand[A] => processCreateCommand(c)
      case c: UpdateCommand[A] => processUpdateCommand(c)
      case _ => IO.raiseError(new Exception("Event is not specific enough. Events must create or update."))
    }

  private def processCreateCommand[A <: Aggregate](createCommand: CreateCommand[A]): IO[(A, List[Event[A]])] =
    for {
      _ <- loadEvents(createCommand.id, (events: List[Event[A]]) => events.isEmpty, "Id is already taken.")
      createEvent <- executeCreateCommand(createCommand)
      initialAggregate <- createAggregate(createEvent)
    } yield (initialAggregate, List(createEvent))

  private def processUpdateCommand[A <: Aggregate](updateCommand: UpdateCommand[A]): IO[(A, List[Event[A]])] =
    for {
      events <- loadEvents(Some(updateCommand.id), (events: List[Event[A]]) => events.nonEmpty, "Id not found.")
      t <- splitEvents(events)
      (createEvent, updateEvents) = t
      initialAggregate <- createAggregate(createEvent)
      aggregate <- updateAggregate(updateEvents, initialAggregate)
      newUpdateEvents <- executeUpdateCommand(updateCommand, aggregate)
      newAggregate <- updateAggregate(newUpdateEvents, aggregate)
    } yield (newAggregate, newUpdateEvents)

  private def loadEvents[A <: Aggregate](id: Option[UUID], check: List[Event[A]] => Boolean, message: String): IO[List[Event[A]]] =
    id.map(eventStore.load[A])
      .getOrElse(IO.pure(Nil))
      .ensure(new Exception(message))(check)

  private def splitEvents[A <: Aggregate](events: List[Event[A]]): IO[(CreateEvent[A], List[UpdateEvent[A]])] =
    IO((events.head.asInstanceOf[CreateEvent[A]], events.tail.asInstanceOf[List[UpdateEvent[A]]]))

  private def createAggregate[A <: Aggregate](createEvent: CreateEvent[A]): IO[A] =
    IO.fromEither(createEvent.create())

  private def updateAggregate[A <: Aggregate](updateEvents: List[UpdateEvent[A]], aggregate: A): IO[A] =
    IO.fromEither(updateEvents.foldLeft(aggregate.asRight[Throwable])((either, updateEvent) =>
      either.flatMap(a => updateEvent.validate(a).flatMap(_ => updateEvent.update(a)))
    ))

  private def executeCreateCommand[A <: Aggregate](createCommand: CreateCommand[A]): IO[CreateEvent[A]] =
    IO.fromEither(createCommand.execute())

  private def executeUpdateCommand[A <: Aggregate](updateCommand: UpdateCommand[A], aggregate: A): IO[List[UpdateEvent[A]]] =
    IO.fromEither(updateCommand.validate(aggregate).flatMap(_ => updateCommand.execute(aggregate)))
}

object Dispatcher {
  def apply(eventStore: EventStore, validator: Validator, publisher: Publisher): Dispatcher =
    new Dispatcher(eventStore, validator, publisher)
}
