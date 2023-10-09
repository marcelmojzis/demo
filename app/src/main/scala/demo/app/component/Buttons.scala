package demo.app.component

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.laminar.modifiers.Modifier

object Buttons:
  def bar(buttons: HtmlElement*): HtmlElement =
    div(
      cls := "mt-6 flex items-center justify-center",
      div(cls := "isolate inline-flex space-x-4", buttons)
    )

  def button(label: String, modifiers: Modifier[HtmlElement]*): HtmlElement =
    L.button(
      tpe := "button",
      cls := "rounded-md bg-indigo-600 px-4 py-2 text-base font-semibold text-white shadow-sm " +
        "hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 " +
        "focus-visible:outline-offset-2 focus-visible:outline-indigo-600",
      modifiers,
      label
    )
