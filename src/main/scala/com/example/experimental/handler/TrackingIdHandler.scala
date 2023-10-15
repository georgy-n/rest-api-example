package com.example.experimental.handler

import cats.MonadThrow
import cats.mtl.Ask
import cats.syntax.all._
import com.example.infrastracture.{Context, Handler}

class TrackingIdHandler[F[_]: MonadThrow](trackingIdTC: Ask[F, Context])
    extends Handler[F, Unit, Unit, String] {
  override def handle(id: Unit): F[Either[Unit, String]] = for {
    trackingId <- trackingIdTC.ask
  } yield trackingId.trackingId.asRight[Unit]
}
