package com.example.books.handler

import cats.MonadThrow
import cats.syntax.all._
import com.example.books.domain
import com.example.books.domain._
import com.example.infrastracture.Handler

class GetBookHandler[F[_]: MonadThrow] extends Handler[F, String, BookError, Book] {
  override def handle(id: String): F[Either[BookError, Book]] = id match {
    case "uuid"  => GetBookHandler.book.asRight[BookError].pure[F]
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
}

object GetBookHandler {
  val book: Book = domain.Book("uuid", "foo", 2023, Author("baz"))
}
