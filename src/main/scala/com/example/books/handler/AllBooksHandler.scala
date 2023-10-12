package com.example.books.handler

import com.example.books.domain.Book
import com.example.infrastracture.Handler

class AllBooksHandler[F[_]] extends Handler[F, Unit, Unit , List[Book]] {
  override def handle(input: Unit): F[Either[Unit, List[Book]]] = ???
}
