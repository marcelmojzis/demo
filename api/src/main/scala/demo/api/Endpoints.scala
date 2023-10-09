package demo.api

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.jsoniter.jsonBody

object Endpoints:
  val getMotd: PublicEndpoint[Unit, Unit, Motd, Any] =
    endpoint.get.in("motd").out(jsonBody[Motd])
