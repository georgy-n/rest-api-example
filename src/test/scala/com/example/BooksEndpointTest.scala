package com.example

import cats.effect.IO
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3._
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.integ.cats.effect.CatsMonadError
import sttp.tapir.server.stub.TapirStubInterpreter

import cats.effect.unsafe.implicits.global
import sttp.model.StatusCode

class BooksEndpointTest extends AsyncFlatSpec with Matchers {

  /*
  it should "work" in {
    // given
    val backendStub: SttpBackend[IO, Any] = TapirStubInterpreter(
      Main.customOptions,
      SttpBackendStub[IO, Any](new CatsMonadError[IO])
    )
      .whenServerEndpoint(BooksApi.booksListingServerEndpoint)
      .thenRunLogic()
      .backend()

    // when
    val response = basicRequest
      .get(uri"http://test.com/books/list/all")
      .header("Authorization", "Bearer password")
      .send(backendStub)
      .unsafeRunSync()

    // then
    response.body shouldBe Right(
      """[{"title":"foo","year":2023,"author":{"name":"baz"}}]"""
    )
    response.code shouldBe StatusCode.Ok
  }


   */
}
