package demo.app.component

import com.raquo.laminar.api.L.*

object Alerts:
  def error(error: Signal[String]): HtmlElement =
    div(
      cls := "p-4 mt-6 sm:mx-6 rounded-lg bg-pink-50",
      div(
        cls := "ml-3",
        h3(
          cls := "font-semibold text-pink-700",
          "Uh-oh! Our app just tripped over a digital pebble \uD83D\uDEA7 \uD83D\uDC5F"
        ),
        div(
          cls := "mt-3 text-pink-700",
          ul(
            role := "list",
            cls := "list-disc space-y-1 pl-6",
            li(child.text <-- error)
          )
        )
      )
    )
