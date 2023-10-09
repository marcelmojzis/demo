package demo.api

import scala.language.implicitConversions
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.generic.auto.*
import sttp.tapir.json.jsoniter.jsonBody

object Endpoints:
  val getMotd: PublicEndpoint[Unit, Unit, Motd, Any] =
    endpoint.get.in("motd").out(jsonBody[Motd])

  private given Codec[String, PersonId, TextPlain] =
    Codec.long.map(PersonId.apply)(identity)

  private given Schema[PersonId] =
    Schema.schemaForLong.map(id => Some(PersonId(id)))(identity)

  private val peopleEndpoint =
    endpoint.in("people").errorOut(statusCode).errorOut(plainBody[String])

  val listPeople: PublicEndpoint[Unit, (StatusCode, String), List[Person], Any] =
    peopleEndpoint.get.out(jsonBody[List[Person]])

  val createPerson: PublicEndpoint[PersonUpsert, (StatusCode, String), Person, Any] =
    peopleEndpoint.post.in(jsonBody[PersonUpsert]).out(jsonBody[Person])

  val getPerson: PublicEndpoint[PersonId, (StatusCode, String), Person, Any] =
    peopleEndpoint.get.in(path[PersonId]).out(jsonBody[Person])

  val updatePerson: PublicEndpoint[(PersonId, PersonUpsert), (StatusCode, String), Person, Any] =
    peopleEndpoint.put.in(path[PersonId]).in(jsonBody[PersonUpsert]).out(jsonBody[Person])

  val deletePerson: PublicEndpoint[PersonId, (StatusCode, String), Unit, Any] =
    peopleEndpoint.delete.in(path[PersonId])
