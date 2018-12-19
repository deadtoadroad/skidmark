package com.deadtoadroad.skidmark.cqrs

import java.util.UUID

import cats.effect.IO
import cats.implicits._
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.reflect.Utilities.getRuntimeClassFromClassTag

import scala.collection.immutable.HashMap
import scala.reflect.ClassTag

class Publisher(
  val subscribers: Map[String, Vector[Subscriber]] = new HashMap().withDefaultValue(Vector())
) {

  def subscribe[A <: Aggregate, E <: Event[A] : ClassTag](subscriber: Subscriber): Publisher = {
    val rc = getRuntimeClassFromClassTag[E].toString
    Publisher(subscribers.updated(rc, subscribers(rc) :+ subscriber))
  }

  def publish[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] = {
    val rc = event.getClass.toString
    subscribers(rc).map(_.handle(id, event)).toList.sequence_
  }

  def publish[A <: Aggregate](id: UUID, events: List[Event[A]]): IO[Unit] = {
    events.map(e => publish(id, e)).sequence_
  }
}

object Publisher {
  def apply(subscribers: Map[String, Vector[Subscriber]] = new HashMap().withDefaultValue(Vector())): Publisher =
    new Publisher(subscribers)
}
