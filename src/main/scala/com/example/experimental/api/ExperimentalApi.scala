package com.example.experimental.api

import cats.effect.IO
import cats.syntax.all._
import com.example.books.domain.{Book, CreateBookError}
import com.example.domain.{ApiError, CommonApiError, Unauthorized}
import com.example.experimental.handler.TrackingIdHandler
import com.example.infrastracture.MainEffect.MainEffect
import com.example.infrastracture.{Context, MainEffect, Server}
import com.example.security.SecurityManaga
import com.example.server.basicEndpoint
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.EndpointOutput.OneOfVariant
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.{PartialServerEndpoint, ServerEndpoint}
import sttp.tapir._

import scala.collection.immutable.List


object ExperimentalApi {

  val securityManager = SecurityManaga.default[MainEffect]

  val basicSecurityEndpoint =
    basicEndpoint
      .securityIn(auth.apiKey(header[String]("X-API-KEY"), WWWAuthenticateChallenge.bearer("Bearer")))
      .errorOutVariantsFromCurrent(err =>
        List(
          oneOfVariant(StatusCode.Unauthorized, jsonBody[Unauthorized]),
          oneOfDefaultVariant(err)
        )
      )
      .serverSecurityLogic(ak =>
        securityManager.checkPermissions(ak)
          .run(Context.generate).widen[Either[CommonApiError, Unit]]
      )

  val trackingIdHandler =
    new TrackingIdHandler[MainEffect](MainEffect.trackingIdGetter)

  val securityTrackingIdEndpoint: PartialServerEndpoint[String, Unit, Unit, CommonApiError, String, Any, IO] =
    basicSecurityEndpoint.get
      .in("trackingId")
      .in("security")
      .out(jsonBody[String])
      .errorOutVariantsFromCurrent(err =>
        List(
          oneOfVariant(StatusCode.NotFound,jsonBody[CreateBookError.WrongTitle]),
          oneOfVariant(StatusCode.BadRequest, jsonBody[CreateBookError.BadId]),
          oneOfDefaultVariant(err)
        )
      )

  val trackingIdEndpoint: Endpoint[Unit, Unit, CommonApiError, String, Any] =
    basicEndpoint.get
      .in("trackingId")
      .out(jsonBody[String])
      .errorOutVariantsFromCurrent(err =>
        List(
          oneOfVariant(StatusCode.NotFound, jsonBody[CreateBookError.WrongTitle]),
          oneOfVariant(StatusCode.BadRequest, jsonBody[CreateBookError.BadId]),
          oneOfDefaultVariant(err)
        )
      )

  import cats.syntax.all._

  val trackingIdServerEndpoint: ServerEndpoint[Any, IO] =
    trackingIdEndpoint.serverLogic(s =>
      trackingIdHandler
        .handle(s)
        .widen[Either[CommonApiError, String]]
        .run(Context.generate)
    )

  val trackingIdSecurityServerEndpoint: ServerEndpoint[Any, IO] =
    securityTrackingIdEndpoint.serverLogic(s => t =>
      trackingIdHandler
        .handle(s)
        .widen[Either[CommonApiError, String]]
        .run(Context.generate)
    )
  val mainEffectEndpoints: List[ServerEndpoint[Any, IO]] = List(
    trackingIdServerEndpoint, trackingIdSecurityServerEndpoint
  )

}
