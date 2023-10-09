package demo.service

import cats.effect.IO
import demo.api.*
import sttp.model.StatusCode
import sttp.tapir.server.ServerEndpoint

class PeopleServerEndpoints(service: PeopleService):
  def endpoints: List[ServerEndpoint[Any, IO]] =
    List(
      Endpoints.listPeople.serverLogic(_ => handleError(service.list())),
      Endpoints.createPerson.serverLogic(data => handleError(service.create(data))),
      Endpoints.getPerson.serverLogic(id => handleError(service.get(id))),
      Endpoints.updatePerson.serverLogic((id, data) => handleError(service.update(id, data))),
      Endpoints.deletePerson.serverLogic(id => handleError(service.delete(id)))
    )

  private def handleError[A](io: IO[A]): IO[Either[(StatusCode, String), A]] =
    io.attempt.flatMap:
      case Left(PersonNotFound(id)) => IO.pure(Left(StatusCode.NotFound -> s"Person $id not found"))
      case Left(error)              => IO.raiseError(error)
      case Right(a)                 => IO.pure(Right(a))
