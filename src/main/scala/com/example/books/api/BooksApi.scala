package com.example.books.api

import cats.effect.IO
import cats.syntax.all._
import com.example.books.domain
import com.example.books.domain.{Author, Book}
import com.example.domain.{ApiError, CommonApiError}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.server.ServerEndpoint

object BooksApi {
  object ServiceLogic {
    val book = domain.Book("uuid", "foo", 2023, Author("baz"))

    def getBooks(): IO[List[Book]] =
      IO.raiseError(new Exception("get books failed"))
//      IO.pure(List(book))


    def getBookSimple(id: String): IO[Book] = {
      id match {
        case "uuid" => IO.pure(book)
        case "throw" => IO.raiseError(new Exception("oh no"))
        case "failed" => IO.raiseError(Failed("getting book is failed"))
        case _ => IO.raiseError(NotFound(s"$id is not found"))
      }
    }
  }

  val basicEndpoint: Endpoint[Unit, Unit, CommonApiError, Unit, Any] =
    endpoint.errorOut(
      oneOf[CommonApiError](oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[ApiError])))
    )

  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] =
    endpoint.get
      .in("books" / "list" / "all")
      .out(jsonBody[List[Book]])

  val book: Endpoint[Unit, String, CommonApiError, Book, Any] =
    basicEndpoint.get
      .in("books")
      .in(path[String]("id"))
      .out(jsonBody[Book])
      .errorOutVariants(
        oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound])),
        oneOfVariant(statusCode(StatusCode.ServiceUnavailable).and(jsonBody[Failed]))
      )

  val booksListingServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.booksListing.serverLogicSuccess(_ => ServiceLogic.getBooks())

  val bookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.book
      .serverLogicRecoverErrors(id => ServiceLogic.getBookSimple(id))

  val createBook: PublicEndpoint[Unit, Unit, List[Book], Any] =
    endpoint.get
      .in("books" / "list" / "all")
      .out(jsonBody[List[Book]])


  sealed trait BookError extends CommonApiError
  case class NotFound(what: String) extends BookError
  case class Failed(why: String) extends BookError

}
