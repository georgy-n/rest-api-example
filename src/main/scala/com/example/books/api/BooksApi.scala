package com.example.books.api

import cats.effect.IO
import cats.syntax.all._
import com.example.books.domain
import com.example.books.domain.{Author, Book}
import com.example.books.service._
import com.example.domain.{ApiError, CommonApiError}
import com.example.server.basicEndpoint
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.server.ServerEndpoint

object BooksApi {

  val handler = new BookHandlerImpl[IO]

  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] =
    endpoint.get
      .in("books" / "list" / "all")
      .out(jsonBody[List[Book]])

  val book: PublicEndpoint[String, CommonApiError, Book, Any] =
    basicEndpoint.get
      .in("books")
      .in(path[String]("id"))
      .out(jsonBody[Book])
      .errorOutVariants(
        oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound])),
        oneOfVariant(statusCode(StatusCode.ServiceUnavailable).and(jsonBody[Failed]))
      )

  val booksListingServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.booksListing.serverLogicSuccess(_ => handler.getBooks)

  val bookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.book.serverLogic(id => handler.getBook(id).widen[Either[CommonApiError, Book]])

  val createBook: PublicEndpoint[Unit, Unit, List[Book], Any] =
    endpoint.get
      .in("books" / "list" / "all")
      .out(jsonBody[List[Book]])


  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(booksListingServerEndpoint, bookServerEndpoint)
}
