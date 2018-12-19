package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.helpers.{ForgetfulEventStore, HappyValidator, InMemoryEventStore}
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.cqrs.model.{Aggregate, Id}
import org.scalatest.AsyncFunSuite

class DispatcherSuite extends AsyncFunSuite {
  val badLoadEventStore: EventStore = new ForgetfulEventStore {
    override def load[A <: Aggregate](id: UUID): IO[List[Event[A]]] =
      IO.raiseError(new Exception("Bad load."))
  }

  val badValidateValidator: Validator = new Validator {
    override def validate[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] =
      IO.raiseError(new Exception("Bad validate."))
  }

  val badSaveEventStore: EventStore = new ForgetfulEventStore {
    override def save[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] =
      IO.raiseError(new Exception("Bad save."))
  }

  val badPublishPublisher: Publisher = new Publisher {
    override def publish[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] =
      IO.raiseError(new Exception("Bad publish."))
  }

  def addThingCreated(eventStore: EventStore): EventStore = {
    eventStore
      .save(Id.default, List(ThingCreated(id = Id.default, name = None)))
      .unsafeRunSync()
    eventStore
  }

  def addThingUpdated(version: Int)(eventStore: EventStore): EventStore = {
    eventStore
      .save(Id.default, List(ThingUpdated(id = Id.default, version = version, name = None)))
      .unsafeRunSync()
    eventStore
  }

  test("dispatcher returns throwable when event store has bad load") {
    val dispatcher = Dispatcher(badLoadEventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(CreateThing(id = Some(Id.default)))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Bad load."))
  }

  test("dispatcher returns throwable when validator has bad validate") {
    val dispatcher = Dispatcher(ForgetfulEventStore(), badValidateValidator, Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(CreateThing(id = Some(Id.default)))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Bad validate."))
  }

  test("dispatcher returns throwable when event store has bad save") {
    val dispatcher = Dispatcher(badSaveEventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(CreateThing(id = None))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Bad save."))
  }

  test("dispatcher returns throwable when publisher has bad publish") {
    val dispatcher = Dispatcher(ForgetfulEventStore(), HappyValidator(), badPublishPublisher)
    recoverToExceptionIf[Exception](dispatcher.dispatch(CreateThing(id = None))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Bad publish."))
  }

  test("dispatcher returns aggregate for create command") {
    val dispatcher = Dispatcher(ForgetfulEventStore(), HappyValidator(), Publisher())
    dispatcher.dispatch(CreateThing(id = None))
      .unsafeToFuture()
      .map(_ => assert(true))
  }

  test("dispatcher returns throwable for update command with taken id") {
    val eventStore = addThingCreated(InMemoryEventStore())
    val dispatcher = Dispatcher(eventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(CreateThing(id = Some(Id.default)))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Id is already taken."))
  }

  test("dispatcher returns aggregate for update command") {
    val eventStore = addThingCreated(InMemoryEventStore())
    val dispatcher = Dispatcher(eventStore, HappyValidator(), Publisher())
    dispatcher.dispatch(UpdateThing(id = Id.default, version = 0))
      .unsafeToFuture()
      .map(_ => assert(true))
  }

  test("dispatcher returns throwable for update command with missing event stack") {
    val dispatcher = Dispatcher(ForgetfulEventStore(), HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(UpdateThing(id = Id.default, version = 0))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Id not found."))
  }

  test("dispatcher returns throwable for update command with incorrect version (one event)") {
    val eventStore = addThingCreated(InMemoryEventStore())
    val dispatcher = Dispatcher(eventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(UpdateThing(id = Id.default, version = 1))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Command version does not match aggregate version."))
  }

  test("dispatcher returns throwable for update command with incorrect version (two events)") {
    val eventStore = (addThingCreated _).andThen(addThingUpdated(1))(InMemoryEventStore())
    val dispatcher = Dispatcher(eventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(UpdateThing(id = Id.default, version = 0))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Command version does not match aggregate version."))
  }

  test("dispatcher returns throwable for update command with bad event stack (out of sequence versions)") {
    val eventStore = (addThingCreated _).andThen(addThingUpdated(2))(InMemoryEventStore())
    val dispatcher = Dispatcher(eventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[Exception](dispatcher.dispatch(UpdateThing(id = Id.default, version = 1))
      .unsafeToFuture())
      .map(t => assert(t.getMessage === "Event version does not match aggregate version."))
  }

  test("dispatcher returns throwable for update command with bad event stack (no create event, update event)") {
    val eventStore = addThingUpdated(0)(InMemoryEventStore())
    val dispatcher = Dispatcher(eventStore, HappyValidator(), Publisher())
    recoverToExceptionIf[ClassCastException](dispatcher.dispatch(UpdateThing(id = Id.default, version = 0))
      .unsafeToFuture())
      .map(t => assert(t.getMessage.contains("cannot be cast")))
  }
}
