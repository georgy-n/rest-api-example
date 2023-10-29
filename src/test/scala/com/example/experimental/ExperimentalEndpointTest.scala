package com.example.experimental

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.example.Main
import com.example.books.domain.{CreateBookError, DeleteBookError}
import com.example.domain.ApiError
import com.example.experimental.api.ExperimentalApi
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3._
import sttp.client3.testing.SttpBackendStub
import sttp.model.StatusCode
import sttp.tapir.integ.cats.effect.CatsMonadError
import sttp.tapir.server.stub.TapirStubInterpreter
/*
class ExperimentalEndpointTest extends AsyncFlatSpec with Matchers {

  private val stubTrackingIdEndpoint = TapirStubInterpreter(
    Main.customOptions,
    SttpBackendStub[IO, Any](new CatsMonadError[IO])
  )
    .whenEndpoint(ExperimentalApi.trackingIdEndpoint)

  private def mkResponse(backendStub: SttpBackend[IO, Any]): Response[Either[String, String]] = basicRequest
    .get(uri"http://test.com/trackingId")
    .header("Authorization", "Bearer password")
    .send(backendStub)
    .unsafeRunSync()


  it should "throw WrongTitle" in {
    // given
    val backendStub: SttpBackend[IO, Any] =
      stubTrackingIdEndpoint
        .thenRespondError(CreateBookError.WrongTitle("wrong title"))
        .backend()

    // when
    val response = mkResponse(backendStub)

    response.code shouldBe StatusCode.NotFound
    response.body shouldBe Left("""{"what":"wrong title"}""")

  }

  it should "throw BadId" in {
    // given
    val backendStub: SttpBackend[IO, Any] =
      stubTrackingIdEndpoint
        .thenRespondError(CreateBookError.BadId("bad id"))
        .backend()

    // when
    val response = mkResponse(backendStub)

    response.code shouldBe StatusCode.BadRequest
  }

  it should "throw another error" in {
    // given
    val backendStub: SttpBackend[IO, Any] = stubTrackingIdEndpoint
      .thenRespondError(DeleteBookError.NotFound("bad id"))
      .backend()

    // when
    val response = mkResponse(backendStub)

    response.code shouldBe StatusCode(1337)
  }

  it should "throw ApiError" in {
    // given
    val backendStub: SttpBackend[IO, Any] = stubTrackingIdEndpoint
      .thenRespondError(ApiError("bad id"))
      .backend()

    // when
    val response = mkResponse(backendStub)

    response.code shouldBe StatusCode.InternalServerError
  }
}


 */