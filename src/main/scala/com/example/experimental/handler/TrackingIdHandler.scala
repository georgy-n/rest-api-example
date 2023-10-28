package com.example.experimental.handler

import cats.MonadThrow
import cats.mtl.Ask
import cats.syntax.all._
import com.example.books.domain.CreateBookError
import com.example.infrastracture.{Context, Handler}

class TrackingIdHandler[F[_]: MonadThrow](trackingIdTC: Ask[F, Context])
    extends Handler[F, Unit, CreateBookError, String] {
  override def handle(id: Unit): F[Either[CreateBookError, String]] = {
    trackingIdTC
      .ask
      .map(_.trackingId)
      .map(CreateBookError.WrongTitle(_).asLeft[String])
      .widen[Either[CreateBookError, String]]
  }
}
