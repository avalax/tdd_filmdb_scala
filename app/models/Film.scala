package models

case class Film (id: FilmId, name: String, genre: String, rating: Int, year: Int)

object Film {
    var films = Set(
        Film(FilmId(1),"film1","action",1,2018)
    )

    def findAll = films.toList.sortBy(_.id.id)
}