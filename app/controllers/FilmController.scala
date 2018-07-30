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
    Ok(views.html.product(filmForm))
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

  def update(id: Long) = Action.async { implicit request =>
    def idOfFilm(insertedId: Option[Long]) = {
      insertedId.getOrElse(id)
    }

    def createFilm(form: FilmForm) = {
      Film(id, form.name, form.genre, form.rating, form.year)
    }

    def validateAndSave = {
      filmForm.bindFromRequest().fold(
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

    repo.findById(id).flatMap {
      case Some(_) => validateAndSave
      case None => Future.successful(NotFound)
    }
  }

  def save = Action.async { implicit request =>
    def idOfFilm(insertedId: Option[Long]) = {
      insertedId.get
    }

    def createFilm(form: FilmForm) = {
      Film(0L, form.name, form.genre, form.rating, form.year)
    }

    filmForm.bindFromRequest().fold(
      hasErrors = { form =>
        Future.successful(Ok(views.html.product(form.withGlobalError("error.check.form"))))
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
    repo.findById(id).flatMap {
      case Some(_) => repo.delete(id).map(_ => Ok)
      case None => Future.successful(NotFound)
    }
  }
}