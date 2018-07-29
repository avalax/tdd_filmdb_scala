package models

case class FilmForm(name: String, genre: String, rating: Int, year: Int)

object FilmForm {
  def createForm(film: Film) = {
    FilmForm(film.name, film.genre, film.rating, film.year)
  }

  def createFilm(form: FilmForm, id: Long) = {
    Film(id, form.name, form.genre, form.rating, form.year)
  }
}