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
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.model.StatusCode
import sttp.monad.MonadError
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.exception.{ExceptionContext, ExceptionHandler}
import sttp.tapir.server.model.ValuedEndpointOutput

object Main extends IOApp {

  val apiEndpoints: List[ServerEndpoint[Any, IO]] = List(BooksApi.booksListingServerEndpoint, BooksApi.bookServerEndpoint)

  val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "rest-api-example", "1.0.0")
  val all: List[ServerEndpoint[Any, IO]] = apiEndpoints ++ docEndpoints

  case class MyFailure(msg: String)
  def myFailureResponse(m: String): ValuedEndpointOutput[_] =
    ValuedEndpointOutput(jsonBody[MyFailure], MyFailure(m))
  override def run(args: List[String]): IO[ExitCode] = {
    val serverOptions: Http4sServerOptions[IO] =
      Http4sServerOptions
        .customiseInterceptors[IO]
        .defaultHandlers(myFailureResponse)
        .exceptionHandler( new ExceptionHandler[IO] {
          override def apply(ctx: ExceptionContext)(implicit monad: MonadError[IO]): IO[Option[ValuedEndpointOutput[_]]] =
            IO.pure(ValuedEndpointOutput(statusCode.and(stringBody), (StatusCode.InternalServerError, ctx.e.toString)).some)
        })
        .options

    val routes = Http4sServerInterpreter[IO](serverOptions)
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
          _ <- IO.println(s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit.")
          _ <- IO.readLine
        } yield ()
      }
      .as(ExitCode.Success)
  }
}
