package com.example.experimental.api

import cats.effect.IO
import com.example.experimental.handler.TrackingIdHandler
import com.example.infrastracture.{MainEffect, Server}
import com.example.infrastracture.MainEffect.MainEffect
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{PublicEndpoint, endpoint}

object ExperimentalApi {

  val trackingIdHandler =
    new TrackingIdHandler[MainEffect](MainEffect.trackingIdGetter)
  val trackingIdEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get
      .in("trackingId")
      .out(jsonBody[String])

  val trackingIdServerEndpoint: ServerEndpoint[Any, IO] =
    Server.richLogic(trackingIdEndpoint, trackingIdHandler)

  val mainEffectEndpoints: List[ServerEndpoint[Any, IO]] = List(
    trackingIdServerEndpoint
  )

}
