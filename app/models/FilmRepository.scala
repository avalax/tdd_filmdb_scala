package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FilmRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class FilmTable(tag: Tag) extends Table[Film](tag, "films") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def genre = column[String]("genre")

    def rating = column[Int]("rating")

    def year = column[Int]("year")

    def * = (id, name, genre, rating, year) <> ((Film.apply _).tupled, Film.unapply)
  }

  private val films = TableQuery[FilmTable]

  def list(): Future[Seq[Film]] = db.run {
    films.result
  }

  def findById(id: Long): Future[Option[Film]] = db.run {
    films.filter(_.id === id).result.headOption
  }

  def delete(id: Long) = db.run {
    films.filter(_.id === id).delete
  }

  def save(film: Film) = db.run {
    //TODO: include update here
    films += film
  }

  private def update(film: Film): Future[Int] = db.run {
    films.filter(_.id === film.id).update(film)
  }
}
