package com.deadtoadroad.skidmark.api.services

import cats.effect.IO
import com.deadtoadroad.skidmark.api.services.serialisers._
import com.deadtoadroad.skidmark.cqrs.Dispatcher
import com.deadtoadroad.skidmark.model.commands.post.{CreatePost, PublishPost, UnpublishPost, UpdatePost}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.log4s.{Logger, getLogger}

class AuthorService(dispatcher: Dispatcher) extends Service {
  override val service: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req@POST -> Root / "post" =>
        req.as[CreatePost]
          .flatMap(dispatcher.dispatch)
          .flatMap(post => Ok(post.asJson))
          .handleErrorWith(handleError)
      case req@PUT -> Root / "post" =>
        req.as[UpdatePost]
          .flatMap(dispatcher.dispatch)
          .flatMap(post => Ok(post.asJson))
          .handleErrorWith(handleError)
      case req@PUT -> Root / "post" / "publish" =>
        req.as[PublishPost]
          .flatMap(dispatcher.dispatch)
          .flatMap(post => Ok(post.asJson))
          .handleErrorWith(handleError)
      case req@PUT -> Root / "post" / "unpublish" =>
        req.as[UnpublishPost]
          .flatMap(dispatcher.dispatch)
          .flatMap(post => Ok(post.asJson))
          .handleErrorWith(handleError)
    }

  override val logger: Logger = getLogger
}

object AuthorService {
  def apply(dispatcher: Dispatcher): Service = new AuthorService(dispatcher)
}
