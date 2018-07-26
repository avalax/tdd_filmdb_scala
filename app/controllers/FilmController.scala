package controllers

import javax.inject.{Inject, Singleton}
import models.{Film, FilmRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, ControllerComponents, Flash}

import scala.concurrent.ExecutionContext

@Singleton
class FilmController @Inject()(repo: FilmRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val filmForm = Form(
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "genre" -> nonEmptyText,
      "rating" -> number(1, 3),
      "year" -> number
    )(Film.apply)(Film.unapply)
  )

  def show(id: Long) = Action.async { implicit request =>
    repo.findById(id).map {
      case Some(f) => Ok(views.html.product(filmForm.fill(f)))
      case None => NotFound
    }
  }

  def create = Action { implicit request =>
    val form = if (request2flash.get("error").isDefined)
      filmForm.bind(request2flash.data)
    else
      filmForm
    Ok(views.html.product(form))
  }

  def save = Action { implicit request =>
    val newFilmForm = filmForm.bindFromRequest()

    newFilmForm.fold(
      hasErrors = { form =>
        Redirect(routes.FilmController.create()).
          flashing(Flash(form.data) +
            ("error" -> form.errors.mkString(", ")))
      },

      success = { newFilm =>
        repo.save(newFilm)
        //TODO: Redirect to film instead, using the new id from save: repo.save(newFilm).map( film => Redirect...)
        Redirect(routes.HomeController.index()).
          flashing("success" -> Messages("Success"))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    repo.delete(id)
    Redirect(routes.HomeController.index())
      .flashing("success" -> Messages("film deleted"))
  }
}
