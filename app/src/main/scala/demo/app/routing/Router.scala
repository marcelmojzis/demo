package demo.app.routing

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.eventPropToProcessor
import com.raquo.laminar.modifiers.*
import com.raquo.waypoint.{Router as WaypointRouter, *}
import demo.api.PersonId
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import scala.language.implicitConversions

object Router:
  private val basePath = "/app"

  private val motdRoute =
    Route.static[Page.Motd.type](Page.Motd, root / "motd" / endOfSegments, basePath)

  private val peopleRoute =
    Route.static[Page.People.type](Page.People, root / "people" / endOfSegments, basePath)

  private val newPersonRoute =
    Route.static[Page.NewPerson.type](
      Page.NewPerson,
      root / "people" / "new" / endOfSegments,
      basePath
    )

  private val personIdSegment = segment[Long].as(id => PersonId(id), identity)

  private val editPersonRoute =
    Route[Page.EditPerson, PersonId](
      encode = page => page.id,
      decode = id => Page.EditPerson(id),
      pattern = root / "people" / personIdSegment / "edit" / endOfSegments,
      basePath = basePath
    )

  private given JsonValueCodec[Page] = JsonCodecMaker.make

  private val router = WaypointRouter[Page](
    routes = List(motdRoute, peopleRoute, newPersonRoute, editPersonRoute),
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
    case Page.Motd          => "Demo: Message of the day"
    case Page.People        => "Demo: People"
    case Page.NewPerson     => "Demo: Create person"
    case _: Page.EditPerson => "Demo: Edit person"

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
