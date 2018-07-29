package controllers

import javax.inject.{Inject, Singleton}
import models.FilmForm.createForm
import models.{FilmForm, FilmRepository}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, ControllerComponents, Flash}

import scala.concurrent.ExecutionContext

@Singleton
class FilmController @Inject()(repo: FilmRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val filmForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "genre" -> nonEmptyText,
      "rating" -> number(1, 3),
      "year" -> number
    )(FilmForm.apply)(FilmForm.unapply)
  )

  def show(id: Long) = Action.async { implicit request =>
    repo.findById(id).map {
      case Some(f) => Ok(views.html.product(filmForm.fill(createForm(f)), id))
      case None => NotFound
    }
  }

  def newForm = Action { implicit request =>
    val form = if (request2flash.get("error").isDefined)
      filmForm.bind(request2flash.data)
    else
      filmForm
    Ok(views.html.product(form))
  }

  def save(id: Long) = Action { implicit request =>
    val newFilmForm = filmForm.bindFromRequest()

    newFilmForm.fold(
      hasErrors = { form =>
        Ok(views.html.product(form, id)).
          flashing(Flash(form.data) +
            ("error" -> form.errors.mkString(", ")))
      },

      success = { newFilm =>
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
