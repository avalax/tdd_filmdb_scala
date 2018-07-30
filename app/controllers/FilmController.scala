package controllers

import javax.inject.{Inject, Singleton}
import models.{Film, FilmForm, FilmRepository}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number}
import play.api.i18n.{I18nSupport, Messages, MessagesProvider}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FilmController @Inject()(repo: FilmRepository, cc: ControllerComponents)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  val filmForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "genre" -> nonEmptyText,
      "rating" -> number(1, 3),
      "year" -> number
    )(FilmForm.apply)(FilmForm.unapply)
  )

  def newForm: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.product(filmForm))
  }

  def show(id: Long): Action[AnyContent] = Action.async { implicit request =>
    def formFromFilm(film: Film) = {
      FilmForm(film.name, film.genre, film.rating, film.year)
    }

    repo.findById(id).map {
      case Some(f) => Ok(views.html.product(filmForm.fill(formFromFilm(f)), id))
      case None => NotFound
    }
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    repo.findById(id).flatMap {
      case Some(_) => validateAndSave(id, filmForm.bindFromRequest())
      case None => Future.successful(NotFound)
    }
  }

  def save: Action[AnyContent] = Action.async { implicit request =>
    validateAndSave(0L, filmForm.bindFromRequest())
  }

  def delete(id: Long): Action[AnyContent] = Action.async { implicit request =>
    repo.findById(id).flatMap {
      case Some(_) => repo.delete(id).map(_ => Ok)
      case None => Future.successful(NotFound)
    }
  }

  private def validateAndSave(id: Long, form: Form[FilmForm])
                             (implicit messagesProvider: MessagesProvider,
                              requestHeader: RequestHeader) = {
    def filmFromForm(form: FilmForm) = {
      Film(id, form.name, form.genre, form.rating, form.year)
    }

    form.fold(
      hasErrors = { form =>
        Future.successful(Ok(views.html.product(form.withGlobalError("error.check.form"), id)))
      },
      success = { newFilm =>
        repo.save(filmFromForm(newFilm)).map(id =>
          Redirect(routes.FilmController.show(id))
            .flashing("success" -> Messages("Success"))
        )
      }
    )
  }
}
