package com.example

import cats.effect.unsafe.IORuntime
import cats.effect.{ExitCode, IO}
import cats.syntax.all._
import com.comcast.ip4s.{Host, Port}
import com.example.books.api.BooksApi
import com.example.domain.ApiError
import com.example.experimental.api.ExperimentalApi
import io.circe.generic.auto._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{DefaultServiceErrorHandler, Router}
import sttp.model.StatusCode
import sttp.monad.MonadError
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler
import sttp.tapir.server.interceptor.exception.{ExceptionContext, ExceptionHandler}
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object MainEffectMain extends App {
  implicit val ioRuntime = IORuntime.builder().build()

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](ExperimentalApi.mainEffectEndpoints, "rest-api-example", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = ExperimentalApi.mainEffectEndpoints ++ docEndpoints

  val customOptions = Http4sServerOptions
    .customiseInterceptors[IO]
//    .defaultHandlers(myFailureResponse)
    .exceptionHandler(new ExceptionHandler[IO] {
      override def apply(ctx: ExceptionContext)(implicit
          monad: MonadError[IO]
      ): IO[Option[ValuedEndpointOutput[_]]] =
        monad.unit(
          ValuedEndpointOutput(
            statusCode.and(jsonBody[ApiError]),
            (StatusCode.VariantAlsoNegotiates, ApiError(ctx.e.toString))
          ).some
        )
    })

    val routes = Http4sServerInterpreter[IO](customOptions.options)
      .toRoutes(all)

    val port = 1337

    val server = EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("localhost").get)
      .withPort(Port.fromInt(port).get)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use(_ => (IO.never))
      .as(ExitCode.Success)

    server.unsafeRunSync()
}
