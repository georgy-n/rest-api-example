package com.example

import cats.data.ReaderT
import cats.effect.IO
import cats.effect.kernel.Sync
import cats.effect.unsafe.IORuntime
import cats.mtl.Ask

object Examples extends App {


  type MainEffect[A] = ReaderT[IO, Context, A]
  object MainEffect {
  }

  val ask = Ask[MainEffect, Context]

  implicit val ioRuntime = IORuntime.builder().build()

  case class Context(trackingId: Int)

  class Foo[F[_]: Sync](askInner: Ask[F, Context]) {
    import cats.syntax.all._

    val busines = for {
      tracking <- askInner.reader(_.trackingId)
    } yield tracking

  }

  val foo = new Foo[MainEffect](ask)
  println(foo.busines.run(Context(123)).unsafeRunSync())
}
