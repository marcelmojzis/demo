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

  private enum Type:
    case Primary
    case Submit
    case Plain

  private def button(tpe: Type, label: String, modifiers: Modifier[Button]*): HtmlElement =
    val htmlTpe = tpe match
      case Type.Submit => "submit"
      case _           => "button"

    val style = tpe match
      case Type.Primary | Type.Submit =>
        "bg-indigo-600 text-white hover:bg-indigo-500"

      case Type.Plain =>
        "bg-white text-gray-900 ring-1 ring-gray-300 hover:bg-gray-50"

    L.button(
      L.tpe := htmlTpe,
      cls := "rounded-md px-4 py-2 text-base font-semibold shadow-sm " +
        "focus-visible:outline focus-visible:outline-2 " +
        "focus-visible:outline-offset-2 focus-visible:outline-indigo-600",
      cls := style,
      modifiers,
      label
    )

  def primary(label: String, modifiers: Modifier[Button]*): HtmlElement =
    button(tpe = Type.Primary, label, modifiers)

  def submit(label: String, modifiers: Modifier[Button]*): HtmlElement =
    button(tpe = Type.Submit, label, modifiers)

  def plain(label: String, modifiers: Modifier[Button]*): HtmlElement =
    button(tpe = Type.Plain, label, modifiers)

  def link(label: String, modifiers: Modifier[Anchor]*): HtmlElement =
    a(
      cls := "text-indigo-600 font-semibold hover:text-indigo-500 focus-visible:outline " +
        "focus-visible:outline-none focus-visible:underline",
      href := "#",
      modifiers,
      label
    )
