package com.example.infrastracture

import cats.data.ReaderT
import cats.effect.IO
import cats.mtl.Ask
import cats.syntax.all._

object MainEffect {
  type MainEffect[A] = ReaderT[IO, Context, A]

  val trackingIdGetter: Ask[MainEffect, Context] = Ask[MainEffect, Context]
}
