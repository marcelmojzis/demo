package demo.app

import com.raquo.laminar.api.L.*
import com.raquo.waypoint.SplitRender
import demo.app.component.Tabs as T
import demo.app.page.*
import demo.app.routing.{Page, Router}
import org.scalajs.dom

@main
def main(): Unit =
  def container(): dom.Element =
    dom.document.getElementById(elementId = "app")

  def app(): HtmlElement =
    div(
      cls := "mx-auto max-w-3xl py-4 px-12",
      div(
        T.bar(
          T.button(label = "Message of the day", Page.Motd),
          T.button(label = "People", Page.People)
        ),
        child <-- renderPage()
      )
    )

  def renderPage(): Signal[HtmlElement] =
    SplitRender[Page, HtmlElement](Router.currentPageSignal)
      .collectStatic(Page.Motd)(MotdPage())
      .collectStatic(Page.People)(PeoplePage())
      .signal

  renderOnDomContentLoaded(container(), app())
