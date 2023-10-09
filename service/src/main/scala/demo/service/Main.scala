package demo.service

import cats.effect.*
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Main extends IOApp.Simple:
  override def run: IO[Unit] =
    val endpoints = MotdServerEndpoints.endpoints
    val routes = Http4sServerInterpreter[IO]().toRoutes(endpoints)

    BlazeServerBuilder[IO]
      .bindHttp(port = 8080, host = "localhost")
      .withHttpApp(Router("/api" -> routes).orNotFound)
      .resource
      .useForever
