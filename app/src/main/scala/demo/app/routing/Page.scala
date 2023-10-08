package demo.app.routing

import demo.api.PersonId

sealed trait Page

object Page:
  case object Motd extends Page
  case object People extends Page
  case object NewPerson extends Page
  final case class EditPerson(id: PersonId) extends Page
