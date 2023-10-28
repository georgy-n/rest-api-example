package com.example

import cats.syntax.all._
import cats.effect.{ExitCode, IO, IOApp}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import io.circe.generic.auto._
import sttp.tapir.generic.auto._
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import com.comcast.ip4s.{Host, Port}
import com.example.books.api.BooksApi
import com.example.domain.ApiError
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.model.StatusCode
import sttp.monad.MonadError
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.exception.{ExceptionContext, ExceptionHandler}
import sttp.tapir.server.model.ValuedEndpointOutput

object Main extends IOApp {

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](BooksApi.apiEndpoints, "rest-api-example", "1.0.0")

  val all: List[ServerEndpoint[Any, IO]] = BooksApi.apiEndpoints ++ docEndpoints

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
            (StatusCode(1337), ApiError(ctx.e.toString))
          ).some
        )
    })

  override def run(args: List[String]): IO[ExitCode] = {
    val routes = Http4sServerInterpreter[IO](customOptions.options)
      .toRoutes(all)

    val port = 1337

    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("localhost").get)
      .withPort(Port.fromInt(port).get)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use { server =>
        for {
          _ <- IO.println(
            s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit."
          )
          _ <- IO.readLine
        } yield ()
      }
      .as(ExitCode.Success)
  }
}
