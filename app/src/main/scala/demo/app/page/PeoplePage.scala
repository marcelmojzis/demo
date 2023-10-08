package demo.app.page

import com.raquo.laminar.api.L.*
import com.raquo.laminar.modifiers.EventListener
import com.raquo.laminar.nodes.ReactiveHtmlElement
import demo.api.*
import demo.app.Client
import demo.app.component.{Buttons as B, *}
import demo.app.routing.*
import org.scalajs.dom.MouseEvent

object PeoplePage:
  private sealed trait Message
  private object Message:
    case object LoadPeople extends Message
    case class PeopleLoaded(people: List[Person]) extends Message
    case class DeletePerson(id: PersonId) extends Message
    case class PersonDeleted(id: PersonId) extends Message

  private sealed trait Command
  private object Command:
    case object LoadPeople extends Command
    case class DeletePerson(id: PersonId) extends Command

  def apply(): HtmlElement =
    val model = Var(List.empty[Person])

    val commandBus = EventBus[Command]()

    val update = Observer[Message]:
      case Message.LoadPeople =>
        commandBus.emit(Command.LoadPeople)
      case Message.PeopleLoaded(people) =>
        model.update(_ => people)
      case Message.DeletePerson(id) =>
        commandBus.emit(Command.DeletePerson(id))
      case Message.PersonDeleted(id) =>
        model.update(_.filter(_.id != id))
    end update

    val processCommands = commandBus.events.flatMap:
      case Command.LoadPeople =>
        Client.listPeople().map(_.getOrElse(Nil)).map[Message](Message.PeopleLoaded.apply)
      case Command.DeletePerson(id) =>
        Client.deletePerson(id).map[Message](_ => Message.PersonDeleted(id))
    end processCommands

    val thStyle = List(cls := "px-3 py-2")
    val tdStyle = List(cls := "whitespace-nowrap px-3 py-3")
    val colStyle = List(cls := "text-gray-500")
    val emColStyle = List(cls := "font-semibold text-gray-900")
    val indexColStyle = emColStyle ++ List(cls := "text-right")

    def renderPeopleOrEmpty(): Signal[HtmlElement] =
      val optPeopleSignal = model.signal.map(people => Option.when(people.nonEmpty)(people))
      optPeopleSignal.splitOption(
        (_, signal) => renderPeople(signal),
        ifEmpty = renderEmpty()
      )

    def renderPeople(peopleSignal: Signal[List[Person]]): HtmlElement =
      div(
        cls := "my-6",
        table(
          cls := "mb-6 min-w-full divide-y divide-gray-300",
          thead(
            tr(
              td(thStyle, indexColStyle, width := "10%", "#."),
              td(thStyle, emColStyle, width := "45%", "Name"),
              td(thStyle, emColStyle, width := "45%", "Email"),
              td(thStyle, width := "0%")
            )
          ),
          tbody(
            cls := "divide-y divide-gray-200",
            children <-- peopleSignal
              .map(_.zipWithIndex)
              .split((person, _) => person.id):
                case (_, (person, _), signal) => renderPerson(person, signal)
          )
        ),
        renderButtonBar()
      )

    def renderPerson(person: Person, personIdxSignal: Signal[(Person, Int)]): HtmlElement =
      tr(
        td(tdStyle, indexColStyle, child.text <-- personIdxSignal.map((_, idx) => s"${idx + 1}.")),
        td(tdStyle, emColStyle, child.text <-- personIdxSignal.map((person, _) => person.name)),
        td(tdStyle, colStyle, child.text <-- personIdxSignal.map((person, _) => person.email)),
        td(
          tdStyle,
          span(
            cls := "flex gap-4",
            B.link(label = "Edit", Router.navigateTo(Page.EditPerson(person.id))),
            B.link(label = "Delete", sendMessageOnClick(Message.DeletePerson(person.id)))
          )
        )
      )

    def sendMessageOnClick(message: Message): EventListener[MouseEvent, Message] =
      onClick.preventDefault.mapTo(message) --> update

    def renderButtonBar(): HtmlElement =
      B.bar(
        B.primary(label = "Add", Router.navigateTo(Page.NewPerson)),
        B.primary(label = "Reload", sendMessageOnClick(Message.LoadPeople))
      )

    def renderEmpty(): HtmlElement =
      div(
        cls := "mx-1 mt-6 p-4 h-64 flex flex-col justify-center rounded-3xl border-2 " +
          "border-dashed border-gray-200",
        p(
          cls := "mx-6 mb-6 text-center",
          "We need more people; someone has to keep the tradition of mismatched socks going."
        ),
        renderButtonBar()
      )

    div(
      EventStream.fromValue(Message.LoadPeople, emitOnce = true) --> update,
      processCommands --> update,
      Headers.page(label = "People"),
      child <-- renderPeopleOrEmpty()
    )
