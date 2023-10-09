package demo.service

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.syntax.all.*
import demo.api.*
import scala.util.control.NoStackTrace

trait PeopleService:
  def list(): IO[List[Person]]
  def create(data: PersonUpsert): IO[Person]
  def get(id: PersonId): IO[Person]
  def update(id: PersonId, data: PersonUpsert): IO[Person]
  def delete(id: PersonId): IO[Unit]

object PeopleService:
  private case class State(people: List[Person], id: PersonId)

  private class PeopleServiceImpl(db: Ref[IO, State]) extends PeopleService:
    override def list(): IO[List[Person]] =
      db.get.map(_.people)

    override def create(data: PersonUpsert): IO[Person] =
      db.modify: state =>
        val person = Person(state.id, data.name, data.email)
        State(state.people :+ person, state.id.next()) -> person

    override def get(id: PersonId): IO[Person] =
      for
        state <- db.get
        person <- IO.fromOption(state.people.find(_.id == id))(PersonNotFound(id))
      yield person

    override def update(id: PersonId, data: PersonUpsert): IO[Person] =
      db.flatModify: state =>
        state.people.indexWhere(_.id == id) match
          case -1 => state -> IO.raiseError(PersonNotFound(id))
          case i =>
            val updated = state.people(i).copy(name = data.name, email = data.email)
            state.copy(people = state.people.updated(i, updated)) -> IO.pure(updated)

    override def delete(id: PersonId): IO[Unit] =
      db.update: state =>
        state.copy(people = state.people.filter(_.id != id))
  end PeopleServiceImpl

  def apply(): IO[PeopleService] =
    Ref[IO].of(State(Nil, PersonId(1L))).map(db => PeopleServiceImpl(db))

final case class PersonNotFound(id: PersonId) extends RuntimeException with NoStackTrace
