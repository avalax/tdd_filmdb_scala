package controllers

import javax.inject._
import models.Film
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  def index() = Action { implicit request =>
    val films = Film.findAll
    Ok(views.html.index(films))
  }
}
