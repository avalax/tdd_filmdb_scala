package controllers

import javax.inject._
import models.Film
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    val films = Film.findAll
    Ok(views.html.index(films))
  }
}
