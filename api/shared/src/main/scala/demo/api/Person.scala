package demo.api

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

final case class Person(id: PersonId, name: String, email: String)

opaque type PersonId = Long

object PersonId:
  def apply(id: Long): PersonId = id

  extension (id: PersonId) def next(): PersonId = id + 1

  given Conversion[PersonId, Long] with
    override def apply(id: PersonId): Long = id

  given JsonValueCodec[PersonId] with
    override def decodeValue(in: JsonReader, default: PersonId): PersonId = PersonId(in.readLong())
    override def encodeValue(id: PersonId, out: JsonWriter): Unit = out.writeVal(id)
    override val nullValue: PersonId = null.asInstanceOf[PersonId]

object Person:
  given JsonValueCodec[Person] = JsonCodecMaker.make
  given JsonValueCodec[List[Person]] = JsonCodecMaker.make

final case class PersonUpsert(name: String, email: String)

object PersonUpsert:
  given JsonValueCodec[PersonUpsert] = JsonCodecMaker.make
