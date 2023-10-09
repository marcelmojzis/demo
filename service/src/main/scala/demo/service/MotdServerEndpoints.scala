package demo.service

import cats.effect.IO
import demo.api.Endpoints
import sttp.tapir.server.ServerEndpoint

object MotdServerEndpoints:
  def endpoints: List[ServerEndpoint[Any, IO]] =
    List(Endpoints.getMotd.serverLogicSuccess(_ => MotdService.random()))
