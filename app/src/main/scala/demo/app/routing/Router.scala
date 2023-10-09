package demo.app.routing

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.eventPropToProcessor
import com.raquo.laminar.modifiers.*
import com.raquo.waypoint.{Router as WaypointRouter, *}
import org.scalajs.dom
import org.scalajs.dom.MouseEvent

object Router:
  private val basePath = "/app"

  private val motdRoute =
    Route.static[Page.Motd.type](Page.Motd, root / "motd" / endOfSegments, basePath)

  private val peopleRoute =
    Route.static[Page.People.type](Page.People, root / "people" / endOfSegments, basePath)

  private given JsonValueCodec[Page] = JsonCodecMaker.make

  private val router = WaypointRouter[Page](
    routes = List(motdRoute, peopleRoute),
    serializePage = page => writeToString(page),
    deserializePage = s => readFromString(s),
    getPageTitle = page => title(page),
    routeFallback = _ => Page.Motd,
    deserializeFallback = _ => Page.Motd
  )(
    popStateEvents = windowEvents(_.onPopState),
    owner = unsafeWindowOwner
  )

  private def title(page: Page): String = page match
    case Page.Motd   => "Demo: Message of the day"
    case Page.People => "Demo: People"

  def currentPageSignal: Signal[Page] = router.currentPageSignal

  def absoluteUrlForPage(page: Page): String = router.absoluteUrlForPage(page)

  def pushPage(page: Page): Unit = router.pushState(page)

  def navigateTo(page: Page): Modifier[HtmlElement] =
    Binder: element =>
      val isLinkElement = element.ref.isInstanceOf[dom.html.Anchor]
      if (isLinkElement) element.amend(href(absoluteUrlForPage(page)))

      def canAccept(event: MouseEvent): Boolean =
        !(isLinkElement && (event.ctrlKey || event.metaKey || event.shiftKey || event.altKey))

      val eventListener = onClick.filter(canAccept).preventDefault --> (_ => pushPage(page))

      eventListener.bind(element)
