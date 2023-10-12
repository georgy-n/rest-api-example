package com.example.books.handler

import com.example.books.domain.{Book, BookForm, CreateBookError}
import com.example.infrastracture.Handler

class CreateBookHandler[F[_]] extends Handler[F, BookForm, CreateBookError, Book] {
  override def handle(input: BookForm): F[Either[CreateBookError, Book]] = ???
}
