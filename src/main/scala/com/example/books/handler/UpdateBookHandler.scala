package com.example.books.handler

import com.example.books.domain.{UpdateBookError, UpdateBookForm}
import com.example.infrastracture.Handler

class UpdateBookHandler[F[_]]
    extends Handler[F, UpdateBookForm, UpdateBookError, Unit] {

  override def handle(input: UpdateBookForm): F[Either[UpdateBookError, Unit]] =
    ???
}
