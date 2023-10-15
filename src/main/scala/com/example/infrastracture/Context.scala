package com.example.infrastracture

import cats.effect.IO
import sttp.tapir.model.ServerRequest

import scala.util.Random

case class Context(trackingId: String)

object Context {
  def parse(serverRequest: ServerRequest): IO[Context] = IO {
    val headers = serverRequest.headers.map(_.toString()).toString
    Context(headers)
  }

  def generate: Context = {
    val rnd = Random.nextString(3)
    Context(rnd)
  }
}
