package com.deadtoadroad.skidmark.git

import java.util.UUID

import cats.effect.IO
import com.deadtoadroad.skidmark.cqrs.Subscriber
import com.deadtoadroad.skidmark.cqrs.model.Aggregate
import com.deadtoadroad.skidmark.cqrs.model.events.Event
import com.deadtoadroad.skidmark.io.GitRepo

class GitSubscriber(root: String, name: String, email: String) extends Subscriber {
  GitRepo(root).ensure().unsafeRunSync()

  override def handle[A <: Aggregate](id: UUID, event: Event[A]): IO[Unit] =
    GitRepo(root).addAllAndCommit(name, email, id.toString)
}

object GitSubscriber {
  def apply(root: String, name: String, email: String): Subscriber = new GitSubscriber(root, name, email)
}
