package com.iridium.domain

final case class AsteroidOutput(
    name: String,
    id: Int
)

final case class ErrorOutput(
    code: Int,
    http_error: String,
    message: String,
    root_cause: String
)
