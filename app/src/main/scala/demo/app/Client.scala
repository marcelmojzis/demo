package demo.app

import com.raquo.airstream.core.EventStream
import demo.api.Endpoints
import scala.concurrent.ExecutionContext.Implicits.global
import sttp.client3.*
import sttp.tapir.client.sttp.SttpClientInterpreter

object Client:
  private val backend = FetchBackend()
  private val interpreter = SttpClientInterpreter()

  private val baseUri = Some(uri"/api")

  def randomMotd(): EventStream[String] =
    val request = interpreter.toRequestThrowErrors(Endpoints.getMotd, baseUri).apply(())
    EventStream.fromFuture(request.send(backend)).map(_.body.message)
