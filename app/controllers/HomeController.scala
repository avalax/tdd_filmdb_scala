package controllers

import javax.inject._
import models.{Film, FilmRepository, State}
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(repo: FilmRepository, cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {
  implicit val filmFormat: OFormat[Film] = Json.format[Film]
  implicit val stateFormat: OFormat[State] = Json.format[State]

  def index: Action[AnyContent] = Action.async { implicit request =>
    repo.list().map { films =>
      Ok(views.html.index(Json.toJson(State(films)), films))
    }
  }
}
