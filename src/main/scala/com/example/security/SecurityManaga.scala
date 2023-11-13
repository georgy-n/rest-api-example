package com.example.security

import cats.Monad
import com.example.domain.{CommonApiError, Unauthorized}

trait SecurityManaga[F[_]] {
  def checkPermissions(token: String): F[Either[Unauthorized, Unit]]
}

object SecurityManaga {
  private class Impl[F[_]: Monad]() extends SecurityManaga[F] {
    override def checkPermissions(token: String): F[Either[Unauthorized, Unit]] = {
      if (token == "unauthorized")
        Monad[F].pure(Left(Unauthorized("Bad token")))
      else
        Monad[F].pure(Right())
    }
  }

  def default[F[_]: Monad]: SecurityManaga[F] = new Impl[F]
}
