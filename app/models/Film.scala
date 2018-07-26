package models

case class Film (id: Long, name: String, genre: String, rating: Int, year: Int)

object Film {
    var films = Set(
        Film(1,"film1","action",2,2018),
        Film(2,"film2","action",3,2016)
    )

    def findById(id: Long) = films.find(_.id == id)
    def add(film: Film) = films = films + film
}