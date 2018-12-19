package com.deadtoadroad.skidmark.api.services

import cats.effect.IO
import com.deadtoadroad.skidmark.api.services.serialisers._
import com.deadtoadroad.skidmark.cqrs.Dispatcher
import com.deadtoadroad.skidmark.model.commands.comment.{UnvetComment, VetComment}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.log4s.{Logger, getLogger}

class AdminService(dispatcher: Dispatcher) extends Service {
  override val service: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req@PUT -> Root / "admin" / "comment" / "vet" =>
        req.as[VetComment]
          .flatMap(dispatcher.dispatch)
          .flatMap(comment => Ok(comment.asJson))
          .handleErrorWith(handleError)
      case req@PUT -> Root / "admin" / "comment" / "unvet" =>
        req.as[UnvetComment]
          .flatMap(dispatcher.dispatch)
          .flatMap(comment => Ok(comment.asJson))
          .handleErrorWith(handleError)
    }

  override val logger: Logger = getLogger
}

object AdminService {
  def apply(dispatcher: Dispatcher): Service = new AdminService(dispatcher)
}
