package demo.api

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

final case class Motd(message: String)

object Motd:
  given JsonValueCodec[Motd] = JsonCodecMaker.make
