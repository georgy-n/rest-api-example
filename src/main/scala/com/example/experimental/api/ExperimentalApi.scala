package com.example.experimental.api

import cats.effect.IO
import com.example.books.domain.{Book, CreateBookError}
import com.example.domain.{ApiError, CommonApiError}
import com.example.experimental.handler.TrackingIdHandler
import com.example.infrastracture.MainEffect.MainEffect
import com.example.infrastracture.{Context, MainEffect, Server}
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir._

object ExperimentalApi {
  val basicEndpoint: Endpoint[Unit, Unit, CommonApiError, Unit, Any] =
    endpoint
      .errorOut(
        oneOf[CommonApiError](
          oneOfDefaultVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[ApiError]))
        )
      )

  val trackingIdHandler =
    new TrackingIdHandler[MainEffect](MainEffect.trackingIdGetter)

  val trackingIdEndpoint: PublicEndpoint[Unit, CommonApiError, String, Any] =
    basicEndpoint.get
      .in("trackingId")
      .out(jsonBody[String])
//      .errorOutVariantPrepend(
//        oneOfVariant(StatusCode.BadRequest, jsonBody[CreateBookError.BadId])
//      )
//      .errorOutVariantPrepend(
//        oneOfVariant(StatusCode.NotFound, jsonBody[CreateBookError.WrongTitle])
//      )
      .errorOutVariants(
        oneOfVariant(StatusCode.NotFound, jsonBody[CreateBookError.WrongTitle]),
        oneOfVariant(StatusCode.BadRequest, jsonBody[CreateBookError.BadId]),
      )

  import cats.syntax.all._

  val trackingIdServerEndpoint: ServerEndpoint[Any, IO] =
    trackingIdEndpoint.serverLogic(s =>
      trackingIdHandler
        .handle(s)
        .widen[Either[CommonApiError, String]]
        .run(Context.generate)
    )

  val mainEffectEndpoints: List[ServerEndpoint[Any, IO]] = List(
    trackingIdServerEndpoint
  )

}
