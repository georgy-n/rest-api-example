package com.example.books.domain

import com.example.domain.CommonApiError

sealed trait CreateBookError extends CommonApiError
object CreateBookError {
  case class WrongTitle(what: String) extends CreateBookError

  case class BadId(why: String) extends CreateBookError
}

sealed trait DeleteBookError extends CommonApiError
object DeleteBookError {
  case class NotFound(id: String) extends BookError

  case class InternalError(why: String) extends BookError
}

sealed trait UpdateBookError extends CommonApiError
object UpdateBookError {
  case class NotFound(id: String) extends BookError

  case class BadId(why: String) extends BookError
}


sealed trait BookError extends CommonApiError
object BookError {
  case class NotFound(what: String) extends BookError

  case class Failed(why: String) extends BookError
}
