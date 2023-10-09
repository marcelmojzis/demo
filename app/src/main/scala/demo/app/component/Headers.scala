package demo.app.component

import com.raquo.laminar.api.L.*

object Headers:
  def page(label: String): HtmlElement =
    h1(cls := "text-3xl font-extrabold text-gray-700 mt-6 mb-2", label)
