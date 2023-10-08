package demo.app.page

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import demo.api.*
import demo.app.Client
import demo.app.component.{Buttons as B, Forms as F, *}
import demo.app.routing.*
import demo.app.syntax.*

object EditPersonPage:
  private sealed trait Model
  private object Model:
    case object InProgress extends Model
    case class Error(error: String) extends Model
    case class Editing(model: PersonModel) extends Model

  private case class PersonModel(
      id: Option[PersonId],
      name: String,
      nameProblem: Option[String],
      email: String,
      emailProblem: Option[String]
  )

  private object PersonModel:
    def apply(): PersonModel =
      PersonModel(id = None, name = "", nameProblem = None, email = "", emailProblem = None)

    def apply(person: Person): PersonModel =
      PersonModel(
        id = Some(person.id),
        name = person.name,
        nameProblem = None,
        email = person.email,
        emailProblem = None
      )

    extension (pm: PersonModel)
      def toPersonUpsert: PersonUpsert = PersonUpsert(name = pm.name, email = pm.email)

  private sealed trait Message
  private object Message:
    case class LoadPerson(id: PersonId) extends Message
    case class PersonLoaded(person: Person) extends Message
    case object SavePerson extends Message
    case object PersonSaved extends Message
    case class OperationFailed(error: String) extends Message

  extension [T](es: EventStream[Either[String, T]])
    private def mapToMessage(f: T => Message): EventStream[Message] =
      es.map(_.fold(Message.OperationFailed.apply, f))

  private sealed trait Command
  private object Command:
    case class LoadPerson(id: PersonId) extends Command
    case class UpsertPerson(data: PersonModel) extends Command

  def apply(id: Option[PersonId]): HtmlElement =
    val (model, initCommand) = id match
      case Some(id) => Var[Model](Model.InProgress) -> Some(Command.LoadPerson(id))
      case None     => Var[Model](Model.Editing(PersonModel())) -> Option.empty[Command]

    val nameWriter = model.updaterPF[String]:
      case (Model.Editing(model), name) => Model.Editing(model.copy(name = name))

    val emailWriter = model.updaterPF[String]:
      case (Model.Editing(model), email) => Model.Editing(model.copy(email = email))

    val commandBus = EventBus[Command]()

    val update = Observer[Message]:
      case Message.LoadPerson(id) =>
        model.set(Model.InProgress)
        commandBus.emit(Command.LoadPerson(id))

      case Message.PersonLoaded(person) =>
        model.updatePF:
          case Model.InProgress => Model.Editing(PersonModel(person))

      case Message.SavePerson =>
        model.updatePF:
          case Model.Editing(model) =>
            commandBus.emit(Command.UpsertPerson(model))
            Model.InProgress

      case Message.PersonSaved =>
        Router.pushPage(Page.People)

      case Message.OperationFailed(error) =>
        model.set(Model.Error(error))
    end update

    val processCommands = commandBus.events.flatMap:
      case Command.LoadPerson(id) =>
        Client.getPerson(id).mapToMessage(Message.PersonLoaded.apply)

      case Command.UpsertPerson(model) =>
        val es = model.id match
          case Some(id) => Client.updatePerson(id, model.toPersonUpsert)
          case None     => Client.createPerson(model.toPersonUpsert)
        es.mapToMessage(_ => Message.PersonSaved)
    end processCommands

    def renderView(): Signal[HtmlElement] =
      model.signal.splitOne(_.getClass):
        case (_, Model.InProgress, _) =>
          renderLoadingView()

        case (_, Model.Error(error), signal) =>
          val errorLoading: PartialFunction[Model, String] =
            case Model.Error(error) => error
          renderErrorView(signal.changes.collect(errorLoading).startWith(error))

        case (_, Model.Editing(model), signal) =>
          val errorLoading: PartialFunction[Model, PersonModel] =
            case Model.Editing(model) => model
          renderEditingView(signal.changes.collect(errorLoading).startWith(model))

    def renderLoadingView(): HtmlElement =
      div(
        Helpers.displayAfterDelay(ms = 500),
        cls := "mt-6 p-4 h-64 flex flex-col justify-center items-center",
        div(F.spinner(svg.cls := "w-12 h-12"))
      )

    def renderErrorView(signal: Signal[String]): HtmlElement =
      div(
        Alerts.error(signal),
        B.bar(B.primary(label = "Close", Router.navigateTo(Page.People)))
      )

    def renderEditingView(signal: Signal[PersonModel]): HtmlElement =
      form(
        cls := "my-6",
        onSubmit.preventDefault.mapTo(Message.SavePerson) --> update,
        div(
          F.section(
            title = "Personal Information",
            description = "The cornerstone of your profile, handled with care and precision.",
            div(
              cls := "mt-4 grid grid-cols-1 gap-x-6 gap-y-6 sm:grid-cols-6",
              F.cell3(
                F.text(
                  id = "name",
                  label = "Name",
                  autoComplete := "name",
                  controlled(value <-- signal.map(_.name), onInput.mapToValue --> nameWriter)
                )
              ),
              F.cell3(
                F.text(
                  id = "email",
                  label = "Email",
                  autoComplete := "email",
                  controlled(value <-- signal.map(_.email), onInput.mapToValue --> emailWriter)
                )
              ),
              F.cell6(F.text(id = "street", label = "Street")),
              F.cell2(F.text(id = "city", label = "City")),
              F.cell2(F.text(id = "province", label = "State / Province")),
              F.cell2(F.text(id = "zip", label = "ZIP / Postal code"))
            )
          ),
          B.bar(
            B.plain(label = "Close", Router.navigateTo(Page.People)),
            B.submit(label = "Save")
          )
        )
      )

    div(
      EventStream.from(initCommand) --> commandBus,
      processCommands --> update,
      Headers.page(id.map(_ => "Edit person").getOrElse("New person")),
      child <-- renderView()
    )
