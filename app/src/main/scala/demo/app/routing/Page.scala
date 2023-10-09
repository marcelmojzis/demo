package demo.app.routing

sealed trait Page

object Page:
  case object Motd extends Page
  case object People extends Page
