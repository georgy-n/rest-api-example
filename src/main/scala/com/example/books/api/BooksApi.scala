package com.example.books.api

import cats.effect.IO
import cats.syntax.all._
import com.example.books.domain._
import com.example.books.handler._
import com.example.domain.CommonApiError
import com.example.server.basicEndpoint
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint

object BooksApi {

  val getBookHandler = new GetBookHandler[IO]
  val allBooksHandler = new AllBooksHandler[IO]
  val deleteBookHandler = new DeleteBookHandler[IO]
  val updateBookHandler = new UpdateBookHandler[IO]
  val createBookHandler = new CreateBookHandler[IO]

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
        oneOfVariant(StatusCode.NotFound, jsonBody[BookError.NotFound]),
        oneOfVariant(StatusCode.ServiceUnavailable, jsonBody[BookError.Failed])
      )

  val createBook: PublicEndpoint[BookForm, CommonApiError, Book, Any] =
    basicEndpoint.post
      .in("books")
      .in(jsonBody[BookForm])
      .out(jsonBody[Book])
      .errorOutVariants(
        oneOfVariant(
          statusCode(StatusCode.NotFound).and(jsonBody[CreateBookError.BadId])
        ),
        oneOfVariant(
          statusCode(StatusCode.BadRequest)
            .and(jsonBody[CreateBookError.WrongTitle])
        )
      )

  val deleteBook: PublicEndpoint[String, CommonApiError, Unit, Any] =
    basicEndpoint.delete
      .in("books")
      .in(path[String]("id"))
      .errorOutVariants(
        oneOfVariant(
          statusCode(StatusCode.NotFound)
            .and(jsonBody[DeleteBookError.NotFound])
        ),
        oneOfVariant(
          statusCode(StatusCode.ServiceUnavailable)
            .and(jsonBody[DeleteBookError.InternalError])
        )
      )

  val updateBook: PublicEndpoint[UpdateBookForm, CommonApiError, Unit, Any] =
    basicEndpoint.patch
      .in("books")
      .in(jsonBody[UpdateBookForm])
      .errorOutVariants(
        oneOfVariant(
          statusCode(StatusCode.NotFound)
            .and(jsonBody[UpdateBookError.NotFound])
        ),
        oneOfVariant(
          statusCode(StatusCode.BadRequest).and(jsonBody[UpdateBookError.BadId])
        )
      )

  val booksListingServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.booksListing.serverLogic(allBooksHandler.handle)

  val bookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.book.serverLogic(id =>
      getBookHandler.handle(id).widen[Either[CommonApiError, Book]]
    )

  val createBookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.createBook.serverLogic(form =>
      createBookHandler.handle(form).widen[Either[CommonApiError, Book]]
    )

  val deleteBookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.deleteBook.serverLogic(id =>
      deleteBookHandler.handle(id).widen[Either[CommonApiError, Unit]]
    )

  val updateBookServerEndpoint: ServerEndpoint[Any, IO] =
    BooksApi.updateBook.serverLogic(form =>
      updateBookHandler.handle(form).widen[Either[CommonApiError, Unit]]
    )

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(
    booksListingServerEndpoint,
    bookServerEndpoint,
    createBookServerEndpoint,
    deleteBookServerEndpoint,
    updateBookServerEndpoint
  )
}
