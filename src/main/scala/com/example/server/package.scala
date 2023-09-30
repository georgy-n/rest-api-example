package com.example

import com.example.domain.{ApiError, CommonApiError}
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

package object server {
  val basicEndpoint: Endpoint[Unit, Unit, CommonApiError, Unit, Any] =
    endpoint.errorOut(
      oneOf[CommonApiError](
        oneOfVariant(
          statusCode(StatusCode.InternalServerError).and(jsonBody[ApiError])
        )
      )
    )
}
