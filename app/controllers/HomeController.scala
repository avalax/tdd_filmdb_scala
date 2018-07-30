package controllers

import javax.inject._
import models.FilmRepository
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(repo: FilmRepository, cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  def index: Action[AnyContent] = Action.async { implicit request =>
    repo.list().map { films =>
      Ok(views.html.index(films))
    }
  }
}
