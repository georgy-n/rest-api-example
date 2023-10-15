package com.example.infrastracture

import com.example.infrastracture.MainEffect.MainEffect
import sttp.tapir.{PublicEndpoint, extractFromRequest}

object Server {
  def richLogic[In, Error, Out](
      endpoint: PublicEndpoint[In, Error, Out, Any],
      handler: Handler[MainEffect, In, Error, Out]
  ) =
    endpoint
      .in(extractFromRequest(identity))
      .serverLogic { case (input, sr) =>
        for {
          context <- Context.parse(sr)
          res <- handler.handle(input).run(context)
        } yield res
      }

}
