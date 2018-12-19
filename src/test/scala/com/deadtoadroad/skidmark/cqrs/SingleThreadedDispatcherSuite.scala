package com.deadtoadroad.skidmark.cqrs

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import cats.data._
import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.all._
import com.deadtoadroad.skidmark.cqrs.helpers.{ForgetfulEventStore, HappyValidator}
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.cqrs.model.{Aggregate, Id}
import org.scalatest.AsyncFunSuite

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class SingleThreadedDispatcherSuite extends AsyncFunSuite {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  test("dispatch processes commands one at a time") {
    val eventStore: EventStore = ForgetfulEventStore()
    val validator: Validator = HappyValidator()
    val counter: AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0)
    val counters: mutable.Map[String, Int] = new scala.collection.concurrent.TrieMap[String, Int]()

    val subscriber: Subscriber = new Subscriber {
      override def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
        event match {
          case thingCreated: ThingCreated =>
            for {
              c <- IO(counter.incrementAndGet())
              _ <- IO.sleep(10.millis)
              _ <- IO(counters += (thingCreated.name.getOrElse("") -> c))
              _ <- IO(counter.decrementAndGet())
            } yield Unit
          case _ => IO.unit
        }
    }

    val publisher: Publisher = Publisher().subscribe[Thing, ThingUpdated](subscriber)
    val dispatcher = SingleThreadedDispatcher(eventStore, validator, publisher)

    def dispatch: String => IO[Thing] =
      (name: String) => dispatcher.dispatch(CreateThing(Some(Id.default), Some(name)))

    NonEmptyList.fromList((1 to 100).toList)
      .get
      .map(_.toString)
      .map(dispatch)
      .parSequence
      .unsafeToFuture()
      .map(_ => assert(counters.values.forall(_ === 1)))
  }
}
