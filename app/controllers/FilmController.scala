package controllers

import javax.inject.{Inject, Singleton}
import models.{Film, FilmId}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

@Singleton
class FilmController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def show(id: Long) = Action { implicit request: Request[AnyContent] =>
    Film.findById(FilmId(id)).map(film =>
      Ok(views.html.product(film))
    ).getOrElse(NotFound)
  }
}
