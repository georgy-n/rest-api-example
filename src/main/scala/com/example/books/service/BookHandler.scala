package com.example.books.service

import cats.MonadThrow
import cats.syntax.all._
import com.example.books.domain
import com.example.books.domain.{Author, Book}
import com.example.domain.CommonApiError

trait BookHandler[F[_]] {
  def getBooks: F[List[Book]]

  def getBook(id: String): F[Either[BookError, Book]]
}

// TODO: separate it for handler and service
class BookHandlerImpl[F[_]: MonadThrow] extends BookHandler[F] {
  private val booksStorage: Map[String, Book] =
    Map("uuid" -> BookHandlerImpl.book)

  override def getBooks: F[List[Book]] = List(BookHandlerImpl.book).pure[F]

  override def getBook(id: String): F[Either[BookError, Book]] = id match {
    case "uuid" => BookHandlerImpl.book.asRight[BookError].pure[F]
    case "throw" => MonadThrow[F].raiseError(new Exception("oh no"))
    case "failed" => Failed("getting book is failed").asLeft[Book].pure[F].widen[Either[BookError, Book]]
    case _ => NotFound(s"$id is not found").asLeft[Book].pure[F].widen[Either[BookError, Book]]
  }


}

sealed trait BookError extends CommonApiError

case class NotFound(what: String) extends BookError

case class Failed(why: String) extends BookError


object BookHandlerImpl {
  val book: Book = domain.Book("uuid", "foo", 2023, Author("baz"))
}