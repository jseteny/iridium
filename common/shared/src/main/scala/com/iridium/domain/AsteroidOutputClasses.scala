package com.iridium.domain

case class AsteroidOutput(
    name: String,
    id: Int
)

case class ErrorOutput(
    code: Int,
    http_error: String,
    message: String,
    root_cause: String
)
