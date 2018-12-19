package com.deadtoadroad.skidmark.api

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import ch.qos.logback.classic.{Level, Logger}
import com.deadtoadroad.skidmark.api.services.AllServices
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{Request, Response}
import org.slf4j.LoggerFactory

object Main extends IOApp {
  // No config yet.
  LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.WARN)
  LoggerFactory.getLogger("com.deadtoadroad.skidmark").asInstanceOf[Logger].setLevel(Level.DEBUG)

  val service: Kleisli[IO, Request[IO], Response[IO]] =
    AllServices("../skidmark-demo", "").service.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
