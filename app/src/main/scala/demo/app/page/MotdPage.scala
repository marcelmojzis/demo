package demo.app.page

import com.raquo.laminar.api.L.*
import demo.app.Client
import demo.app.component.{Buttons as B, *}

object MotdPage:
  private case object Reload

  def apply(): HtmlElement =
    val commandBus = EventBus[Reload.type]()
    val motdSignal = commandBus.events
      .flatMap(_ => Client.randomMotd())
      .toSignal(initial = "Loading...")

    div(
      EventStream.fromValue(Reload) --> commandBus,
      Headers.page(label = "Message of the day"),
      articleTag(
        cls := "prose max-w-none text-justify my-6",
        p(
          "Diving into ",
          em("the Message of the day"),
          " is like unwrapping a tiny digital present every day. It enlightens, entertains, " +
            "and occasionally reminds you of that software update you've been ignoring. " +
            "Today's gem? Brought to you by the word-wizardry of ChatGPT! \uD83C\uDFA9✨"
        ),
        blockQuote(cls := "ml-4", child.text <-- motdSignal),
        p(
          "If these bytes of wisdom tickle your fancy, just smash that reload button. " +
            "Maybe the next message will reveal where you left your keys... or perhaps " +
            "the secrets of the universe. No promises, though! \uD83D\uDE1C"
        )
      ),
      B.bar(
        B.button(label = "Next message", onClick.preventDefault.mapTo(Reload) --> commandBus.writer)
      )
    )
