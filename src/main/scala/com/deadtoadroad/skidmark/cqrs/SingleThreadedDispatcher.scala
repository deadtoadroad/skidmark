package com.deadtoadroad.skidmark.cqrs

import cats.effect.concurrent.Semaphore
import cats.effect.{ContextShift, IO}
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.commands.Command

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

class SingleThreadedDispatcher(eventStore: EventStore, validator: Validator, publisher: Publisher)
  extends Dispatcher(eventStore, validator, publisher) {
  private implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  private val semaphore: Semaphore[IO] = Semaphore[IO](1).unsafeRunSync()

  override def dispatch[A <: Aggregate : ClassTag](command: Command[A]): IO[A] =
    semaphore.withPermit(super.dispatch(command))
}

object SingleThreadedDispatcher {
  def apply(eventStore: EventStore, validator: Validator, publisher: Publisher): Dispatcher =
    new SingleThreadedDispatcher(eventStore, validator, publisher)
}
