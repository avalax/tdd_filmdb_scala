package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FilmRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class FilmTable(tag: Tag) extends Table[Film](tag, "films") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def genre: Rep[String] = column[String]("genre")

    def rating: Rep[Int] = column[Int]("rating")

    def year: Rep[Int] = column[Int]("year")

    def * : ProvenShape[Film] =
      (id, name, genre, rating, year) <> ((Film.apply _).tupled, Film.unapply)
  }

  private val films = TableQuery[FilmTable]

  def list(): Future[Seq[Film]] = db.run {
    films.result
  }

  def findById(id: Long): Future[Option[Film]] = db.run {
    films.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Int] = db.run {
    films.filter(_.id === id).delete
  }

  def save(film: Film):Future[Long] = db.run {
    (films returning films.map(_.id)).insertOrUpdate(film).map(_.getOrElse(film.id))
  }
}
