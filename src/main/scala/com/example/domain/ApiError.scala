package com.example.domain

trait CommonApiError extends Throwable
case class ApiError(defaultCause: String) extends CommonApiError
