package com.iridium.domain

object job {
  case class Job(
      company: String,
      title: String,
      description: String,
      externalUrl: String,
      salaryLo: Option[Int],
      salaryHi: Option[Int],
      currency: Option[String],
      remote: Boolean,
      location: String,
      country: Option[String]
  )

  object Job {
    val dummy = Job(
      "IBM",
      "Instructor",
      "Scala teacher",
      "iridium.com",
      Some(0),
      Some(99),
      Some("EUR"),
      true,
      "Bucharest",
      Some("Romania")
    )
  }
}
