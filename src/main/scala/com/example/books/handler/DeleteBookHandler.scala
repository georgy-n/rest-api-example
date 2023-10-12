package com.example.books.handler

import com.example.books.domain.DeleteBookError
import com.example.infrastracture.Handler

class DeleteBookHandler[F[_]] extends Handler[F, String, DeleteBookError, Unit] {
  override def handle(input: String): F[Either[DeleteBookError, Unit]] = ???
}
