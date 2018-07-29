package controllers

import javax.inject.{Inject, Singleton}
import models.{Film, FilmForm, FilmRepository}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

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

  def newForm = Action { implicit request =>
    val form = if (request2flash.get("error").isDefined)
      filmForm.bind(request2flash.data)
    else
      filmForm
    Ok(views.html.product(form))
  }

  def show(id: Long) = Action.async { implicit request =>
    def createForm(film: Film) = {
      FilmForm(film.name, film.genre, film.rating, film.year)
    }

    repo.findById(id).map {
      case Some(f) => Ok(views.html.product(filmForm.fill(createForm(f)), id))
      case None => NotFound
    }
  }

  def save(id: Long) = Action.async { implicit request =>
    def idOfFilm(insertedId: Option[Long]) = {
      insertedId.getOrElse(id)
    }

    def createFilm(form: FilmForm) = {
      Film(id, form.name, form.genre, form.rating, form.year)
    }

    val newFilmForm = filmForm.bindFromRequest()

    newFilmForm.fold(
      hasErrors = { form =>
        Future.successful(Ok(views.html.product(form.withGlobalError("error.check.form"), id)))
      },

      success = { newFilm =>
        repo.save(createFilm(newFilm)).map(insertedId =>
          Redirect(routes.FilmController.show(idOfFilm(insertedId)))
            .flashing("success" -> Messages("Success"))
        )
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    repo.delete(id).map(_ =>
      Ok
    )
  }
}
