package controllers

import javax.inject.{Inject, Singleton}
import models.Film
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, ControllerComponents, Flash}

@Singleton
class FilmController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  val filmForm = Form(
    mapping(
      "id" -> longNumber.verifying("al", Film.findById(_).isEmpty),
      "name" -> nonEmptyText,
      "genre" -> nonEmptyText,
      "rating" -> number(1, 3),
      "year" -> number
    )(Film.apply)(Film.unapply)
  )

  def show(id: Long) = Action { implicit request =>
    Film.findById(id).map(film =>
      Ok(views.html.product(filmForm.fill(film)))
    ).getOrElse(NotFound)
  }

  def newFilm = Action { implicit request =>
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
        Redirect(routes.FilmController.newFilm()).
          flashing(Flash(form.data) +
            ("error" -> form.errors.mkString(", ")))
      },

      success = { newFilm =>
        Film.add(newFilm)
        Redirect(routes.FilmController.show(newFilm.id)).
          flashing("success" -> Messages("Success"))
      }
    )

  }
}
