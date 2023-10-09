package demo.app

import com.raquo.laminar.api.L.*
import demo.app.page.*
import org.scalajs.dom

@main
def main(): Unit =
  def container(): dom.Element =
    dom.document.getElementById(elementId = "app")

  def app(): HtmlElement =
    div(cls := "mx-auto max-w-3xl py-4 px-12", div(MotdPage()))

  renderOnDomContentLoaded(container(), app())
