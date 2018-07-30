import models.{Film, FilmRepository}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.postfixOps

class ApplicationAcceptanceSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  private def addFilmToRepo(
                             repo: FilmRepository, name: String = "anyName", genre: String = "anyGenre", year: Int = 1, rating: Int = 3) = {
    Await.result(repo.save(Film(0L, name, genre, rating, year)), Duration.Inf)
  }

  "Acceptance Tests" should {

    "show all films from repository" in {
      val repo = inject[FilmRepository]

      val id1 = addFilmToRepo(repo, "Film A")
      val id2 = addFilmToRepo(repo, "Film B")

      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Film A")
      contentAsString(home) must include("Film B")

      repo.delete(id1)
      repo.delete(id2)
    }

    "show film from repository" in {
      val repo = inject[FilmRepository]

      val id = addFilmToRepo(repo, "Film A")

      val request = FakeRequest(GET, "/film/" + id)
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Film A")

      repo.delete(id)
    }

    "show new film form" in {
      val request = FakeRequest(GET, "/film")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("form")
    }

    "add new film to repository" in {
      val repo = inject[FilmRepository]
      val request = FakeRequest(POST, "/film")
        .withFormUrlEncodedBody(("name", "Film A"), ("genre", "Genre A"), ("year", "2018"), ("rating", "2"))
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe SEE_OTHER
      val films = Await.result(repo.list(), Duration.Inf)
      films must have size 1
      films.head.name mustEqual "Film A"
      films.head.genre mustEqual "Genre A"
      films.head.year mustEqual 2018
      films.head.rating mustEqual 2

      repo.delete(films.head.id)
    }

    "validation error during add Film from repository" in {
      val repo = inject[FilmRepository]

      val request = FakeRequest(POST, "/film")
        .withFormUrlEncodedBody(("year", "invalid year"), ("rating", "99"))
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe OK
      val films = Await.result(repo.list(), Duration.Inf)
      films must have size 0
      contentType(filmRoute) mustBe Some("text/html")
      contentAsString(filmRoute) must include("error.check.form")
    }

    "modify Film from repository" in {
      val repo = inject[FilmRepository]

      val id = addFilmToRepo(repo, "oldFilmName", "oldGenreName", 2018, 2)

      val request = FakeRequest(POST, "/film/" + id)
        .withFormUrlEncodedBody(("name", "newFilmName"), ("genre", "newGenreName"), ("year", "1999"), ("rating", "1"))
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe SEE_OTHER
      val films = Await.result(repo.list(), Duration.Inf)
      films must have size 1
      films.head.name mustEqual "newFilmName"
      films.head.genre mustEqual "newGenreName"
      films.head.year mustEqual 1999
      films.head.rating mustEqual 1

      repo.delete(films.head.id)
    }

    "validation error during modify Film from repository" in {
      val repo = inject[FilmRepository]

      val id = addFilmToRepo(repo, "oldFilmName", "oldGenreName", 2018, 2)

      val request = FakeRequest(POST, "/film/" + id)
        .withFormUrlEncodedBody(("year", "invalid year"), ("rating", "99"))
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe OK
      val films = Await.result(repo.list(), Duration.Inf)
      films must have size 1
      films.head.name mustEqual "oldFilmName"
      films.head.genre mustEqual "oldGenreName"
      films.head.year mustEqual 2018
      films.head.rating mustEqual 2
      contentType(filmRoute) mustBe Some("text/html")
      contentAsString(filmRoute) must include("error.check.form")

      repo.delete(films.head.id)
    }

    "delete Film from repository" in {
      val repo = inject[FilmRepository]

      val id = addFilmToRepo(repo)

      val request = FakeRequest(DELETE, "/film/" + id)
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe OK
      val films = Await.result(repo.list(), Duration.Inf)
      films must have size 0
    }

    "unknown Film should result into PageNotFound" in {
      val request = FakeRequest(GET, "/film/404")
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe NOT_FOUND
    }

    "modify unknown Film should result into PageNotFound" in {
      val request = FakeRequest(POST, "/film/404")
        .withFormUrlEncodedBody(("name", "unknownFilmName"))
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe NOT_FOUND
    }

    "delete unknown Film should result into PageNotFound" in {
      val request = FakeRequest(DELETE, "/film/404")
      val filmRoute = route(app, request).get

      status(filmRoute) mustBe NOT_FOUND
    }
  }
}
