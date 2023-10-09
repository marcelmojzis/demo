package demo.app.page

import com.raquo.laminar.api.L.*
import demo.app.component.*

object PeoplePage:
  def apply(): HtmlElement =
    div(Headers.page(label = "People"))
