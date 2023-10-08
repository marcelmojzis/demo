package demo.app.component

import com.raquo.laminar.api.L.*
import com.raquo.laminar.modifiers.Modifier

object Helpers:
  def displayAfterDelay(ms: Int): Modifier[HtmlElement] =
    cls.toggle("hidden") <-- EventStream.delay(ms, event = false).startWith(initial = true)
