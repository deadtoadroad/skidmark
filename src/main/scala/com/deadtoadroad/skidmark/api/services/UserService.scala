package com.deadtoadroad.skidmark.api.services

import cats.effect.IO
import com.deadtoadroad.skidmark.api.services.serialisers._
import com.deadtoadroad.skidmark.cqrs.Dispatcher
import com.deadtoadroad.skidmark.model.commands.comment.{CreateComment, UpdateComment}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.log4s.{Logger, getLogger}

class UserService(dispatcher: Dispatcher) extends Service {
  override val service: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "comment" =>
        req.as[CreateComment]
          .flatMap(dispatcher.dispatch)
          .flatMap(comment => Ok(comment.asJson))
          .handleErrorWith(handleError)
      case req@PUT -> Root / "comment" =>
        req.as[UpdateComment]
          .flatMap(dispatcher.dispatch)
          .flatMap(comment => Ok(comment.asJson))
          .handleErrorWith(handleError)
    }

  override val logger: Logger = getLogger
}

object UserService {
  def apply(dispatcher: Dispatcher): Service = new UserService(dispatcher)
}
