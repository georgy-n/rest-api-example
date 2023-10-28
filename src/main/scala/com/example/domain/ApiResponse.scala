package com.example.domain

sealed trait ApiResponse[+Body]
object ApiResponse {
  case class FailedApiResponse(trackingId: String) extends ApiResponse[Nothing]

  case class SuccessfulApiResponse[Body](body: Option[Body], trackingId: String) extends ApiResponse[Body]
}