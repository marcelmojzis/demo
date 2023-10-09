package demo.app.component

import com.raquo.laminar.api.L.*
import com.raquo.laminar.modifiers.Modifier
import demo.app.routing.*

object Tabs:
  def bar(buttons: HtmlElement*): HtmlElement =
    navTag(cls := "mt-6 flex space-x-4", buttons)

  def button(
      label: String,
      activeSignal: Signal[Boolean],
      modifiers: Modifier[Anchor]*
  ): HtmlElement =
    a(
      cls := "rounded-md px-4 py-2 text-base font-semibold",
      cls <-- activeSignal.map:
        case false =>
          "text-gray-600 bg-white hover:bg-gray-100 hover:text-gray-800 focus-visible:outline " +
            "focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
        case true =>
          "cursor-default pointer-events-none text-indigo-800 bg-indigo-100 hover:text-indigo-800 " +
            "hover:bg-indigo-100 focus:outline-none"
      ,
      tabIndex <-- activeSignal.map: active =>
        if active then -1 else 0,
      modifiers,
      label
    )

  def button(label: String, page: Page): HtmlElement =
    button(label, Router.currentPageSignal.map(_ == page), Router.navigateTo(page))
