package com.deadtoadroad.skidmark.api.services

import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Response}
import org.log4s.Logger

trait Service {
  val service: HttpRoutes[IO]
  val logger: Logger

  def handleError: PartialFunction[Throwable, IO[Response[IO]]] = {
    case e: Exception =>
      logger.error(e)(e.getMessage)
      InternalServerError(e.getMessage)
  }
}
