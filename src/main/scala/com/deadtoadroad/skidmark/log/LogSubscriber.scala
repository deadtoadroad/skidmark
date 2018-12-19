package com.deadtoadroad.skidmark.log

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.Subscriber
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import org.log4s.{Logger, getLogger}

class LogSubscriber(prefix: String) extends Subscriber {
  private val logger: Logger = getLogger

  override def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
    IO(logger.debug(s"$prefix - $event"))
}

object LogSubscriber {
  def apply(prefix: String): LogSubscriber = new LogSubscriber(prefix)
}
