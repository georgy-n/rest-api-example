package com.example.books.service

import cats.Monad
import cats.syntax.all._
import com.example.books.domain
import com.example.books.domain.{Author, Book}

trait BookService[F[_]] {
  def getBooks: F[List[Book]]

  def getBook(id: String): F[Option[Book]]
}

class BookServiceImpl[F[_]: Monad] extends BookService[F] {
  private val booksStorage: Map[String, Book] =
    Map("uuid" -> BookServiceImpl.book)

  override def getBooks: F[List[Book]] = List(BookServiceImpl.book).pure[F]

  override def getBook(id: String): F[Option[Book]] = booksStorage.get(id).pure[F]
}

object BookServiceImpl {
  val book: Book = domain.Book("uuid", "foo", 2023, Author("baz"))
}