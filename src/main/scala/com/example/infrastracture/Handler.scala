package com.example.infrastracture

trait Handler[F[_], I, E, O] {

  def handle(input: I): F[Either[E, O]]
}
