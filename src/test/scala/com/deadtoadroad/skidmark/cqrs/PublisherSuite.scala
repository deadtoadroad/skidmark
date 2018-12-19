package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.cqrs.model.{Aggregate, Id}
import org.scalatest._

class PublisherSuite extends AsyncFunSuite {
  val thingCreated = ThingCreated(id = Id.default, name = None)

  test("publish returns unit when no subscriber") {
    val publisher = Publisher()
    publisher.publish(thingCreated.id, thingCreated)
      .unsafeToFuture()
      .map(_ => assert(true))
  }

  test("publish calls subscriber handle") {
    var handled = false
    val subscriber = new Subscriber {
      override def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
        IO {
          handled = true
        }
    }
    val publisher = Publisher().subscribe[Thing, ThingCreated](subscriber)
    assert(!handled)
    publisher.publish(thingCreated.id, thingCreated)
      .unsafeToFuture()
      .map(_ => assert(handled))
  }
}
