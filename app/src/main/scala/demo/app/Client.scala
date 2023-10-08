package demo.app

import com.raquo.airstream.core.EventStream
import demo.api.*
import scala.concurrent.ExecutionContext.Implicits.global
import sttp.client3.*
import sttp.model.StatusCode
import sttp.tapir.DecodeResult
import sttp.tapir.client.sttp.SttpClientInterpreter

object Client:
  private val backend = FetchBackend()
  private val interpreter = SttpClientInterpreter()

  private val baseUri = Some(uri"/api")

  def randomMotd(): EventStream[String] =
    val request = interpreter.toRequestThrowErrors(Endpoints.getMotd, baseUri).apply(())
    EventStream.fromFuture(request.send(backend)).map(_.body.message)

  def listPeople(): EventStream[Either[String, List[Person]]] =
    val request = interpreter.toRequest(Endpoints.listPeople, baseUri).apply(())
    EventStream.fromFuture(request.send(backend)).map(handleResponse)

  def createPerson(data: PersonUpsert): EventStream[Either[String, Person]] =
    val request = interpreter.toRequest(Endpoints.createPerson, baseUri).apply(data)
    EventStream.fromFuture(request.send(backend)).map(handleResponse)

  def getPerson(id: PersonId): EventStream[Either[String, Person]] =
    val request = interpreter.toRequest(Endpoints.getPerson, baseUri).apply(id)
    EventStream.fromFuture(request.send(backend)).map(handleResponse)

  def updatePerson(id: PersonId, data: PersonUpsert): EventStream[Either[String, Person]] =
    val request = interpreter.toRequest(Endpoints.updatePerson, baseUri).apply(id, data)
    EventStream.fromFuture(request.send(backend)).map(handleResponse)

  def deletePerson(id: PersonId): EventStream[Either[String, Unit]] =
    val request = interpreter.toRequest(Endpoints.deletePerson, baseUri).apply(id)
    EventStream.fromFuture(request.send(backend)).map(handleResponse)

  private type ResponseDecodeResult[T] = Response[DecodeResult[Either[(StatusCode, String), T]]]

  private def handleResponse[T](response: ResponseDecodeResult[T]): Either[String, T] =
    response.body match
      case DecodeResult.Value(Right(t))     => Right(t)
      case DecodeResult.Value(Left((s, e))) => Left(e)
      case DecodeResult.Error(e, _)         => Left(e)
      case _: DecodeResult.Failure          => Left("Cannot decode response")
