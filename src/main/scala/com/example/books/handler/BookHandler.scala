package com.example.books.handler

import cats.MonadThrow
import cats.syntax.all._
import com.example.books.domain
import com.example.books.domain._

trait BookHandler[F[_]] {
  def getBooks: F[List[Book]]

  def getBook(id: String): F[Either[BookError, Book]]

  def createBook(form: BookForm): F[Either[CreateBookError, Book]]

  def deleteBook(id: String): F[Either[DeleteBookError, Unit]]

  def updateBook(form: UpdateBookForm): F[Either[UpdateBookError, Unit]]
}

// TODO: separate it for handler and service
class BookHandlerImpl[F[_]: MonadThrow] extends BookHandler[F] {
  private val booksStorage: Map[String, Book] =
    Map("uuid" -> BookHandlerImpl.book)

  override def getBooks: F[List[Book]] = List(BookHandlerImpl.book).pure[F]

  override def getBook(id: String): F[Either[BookError, Book]] = id match {
    case "uuid"  => BookHandlerImpl.book.asRight[BookError].pure[F]
    case "throw" => MonadThrow[F].raiseError(new Exception("oh no"))
    case "failed" =>
      BookError
        .Failed("getting book is failed")
        .asLeft[Book]
        .pure[F]
        .widen[Either[BookError, Book]]
    case _ =>
      BookError
        .NotFound(s"$id is not found")
        .asLeft[Book]
        .pure[F]
        .widen[Either[BookError, Book]]
  }

  override def createBook(form: BookForm): F[Either[CreateBookError, Book]] =
    ???

  override def deleteBook(id: String): F[Either[DeleteBookError, Unit]] = ???

  override def updateBook(
      form: UpdateBookForm
  ): F[Either[UpdateBookError, Unit]] = ???
}

object BookHandlerImpl {
  val book: Book = domain.Book("uuid", "foo", 2023, Author("baz"))
}
