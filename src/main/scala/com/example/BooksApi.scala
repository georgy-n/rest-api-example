package com.example

import cats.effect.IO
import domain._
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import io.circe.generic.auto._
import sttp.tapir.generic.auto._
import sttp.tapir.server.ServerEndpoint

object BooksApi {
  object ServiceLogic {
    val book = Book("foo", 2023, Author("baz"))

    def getBooks(): IO[List[Book]] = IO.pure(List(book))

    def getBook(id: Int): IO[Book] =
      if (id > 1) IO.raiseError(new Exception("oh no"))
      else IO.pure(book)

  }

  val booksListing: PublicEndpoint[Unit, Unit, List[Book], Any] =
    endpoint.get
      .in("books" / "list" / "all")
      .out(jsonBody[List[Book]])

  val book: Endpoint[Unit, Int, Unit, Book, Any] =
    endpoint.get
      .in("books")
      .in(path[Int]("id"))
      .out(jsonBody[Book])

  val booksListingServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.booksListing.serverLogicSuccess(_ => ServiceLogic.getBooks())

  val bookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.book.serverLogicSuccess(id => ServiceLogic.getBook(id))

  val createBook: PublicEndpoint[Unit, Unit, List[Book], Any] =
    endpoint.get
      .in("books" / "list" / "all")
      .out(jsonBody[List[Book]])

}
