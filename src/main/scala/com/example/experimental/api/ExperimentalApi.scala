package com.example.experimental.api

import cats.effect.IO
import com.example.books.domain.{Book, CreateBookError}
import com.example.domain.{ApiError, CommonApiError, Unauthorized}
import com.example.experimental.handler.TrackingIdHandler
import com.example.infrastracture.MainEffect.MainEffect
import com.example.infrastracture.{Context, MainEffect, Server}
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.{PartialServerEndpoint, ServerEndpoint}
import sttp.tapir._

object ExperimentalApi {
  val basicEndpoint: Endpoint[Unit, Unit, CommonApiError, Unit, Any] =
    endpoint
      .errorOut(
        oneOf[CommonApiError](
          oneOfDefaultVariant(
            statusCode(StatusCode.InternalServerError).and(jsonBody[ApiError])
          )
        )
      )

  val basicSecurityEndpoint =
    basicEndpoint
      .securityIn(auth.apiKey(header[String]("X-API-KEY"), WWWAuthenticateChallenge.bearer("Bearer")))
      .errorOutVariantsFromCurrent(err =>
        List(
          oneOfVariant(StatusCode.Unauthorized, jsonBody[Unauthorized]),
          oneOfDefaultVariant(err)
        )
      )
      .serverSecurityLogic(ak => IO {
        println(ak)
        Left(Unauthorized(ak)): Either[CommonApiError, String]
      })

  val trackingIdHandler =
    new TrackingIdHandler[MainEffect](MainEffect.trackingIdGetter)

  val trackingIdEndpoint: PartialServerEndpoint[String, String, Unit, CommonApiError, String, Any, IO] =
    basicSecurityEndpoint.get
      .in("trackingId")
      .out(jsonBody[String])
      .errorOutVariantsFromCurrent(err =>
        List(
          oneOfVariant(StatusCode.NotFound,jsonBody[CreateBookError.WrongTitle]),
          oneOfVariant(StatusCode.BadRequest, jsonBody[CreateBookError.BadId]),
          oneOfDefaultVariant(err)
        )
      )

  import cats.syntax.all._

  val trackingIdServerEndpoint: ServerEndpoint[Any, IO] =
    trackingIdEndpoint.serverLogic(p => s =>
      trackingIdHandler
        .handle(s)
        .widen[Either[CommonApiError, String]]
        .run(Context.generate)
    )

  val mainEffectEndpoints: List[ServerEndpoint[Any, IO]] = List(
    trackingIdServerEndpoint
  )

}
