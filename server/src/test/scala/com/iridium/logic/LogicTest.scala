package com.iridium.logic

import org.scalatest.flatspec.AnyFlatSpec

class LogicTest extends AnyFlatSpec {

  "makeMax7DaysRanges" should "return a list of tuples with max 7 days inclusive consecutive ranges" in {
    val startDate = java.time.LocalDate.of(2021, 1, 1)
    val endDate = java.time.LocalDate.of(2021, 1, 10)
    val result = makeMax7DaysRanges(startDate, endDate)
    assert(result == List(
      (java.time.LocalDate.of(2021, 1, 1), java.time.LocalDate.of(2021, 1, 8)),
      (java.time.LocalDate.of(2021, 1, 9), java.time.LocalDate.of(2021, 1, 10))
    ))
  }

  it should "return the range itself if it is less than 7 days" in {
    val startDate = java.time.LocalDate.of(2021, 1, 30)
    val endDate = java.time.LocalDate.of(2021, 2, 1)
    val result = makeMax7DaysRanges(startDate, endDate)
    assert(result == List(
      (java.time.LocalDate.of(2021, 1, 30), java.time.LocalDate.of(2021, 2, 1))
    ))
  }
}
