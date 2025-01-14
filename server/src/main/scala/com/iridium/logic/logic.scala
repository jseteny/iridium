package com.iridium.logic

import java.time.LocalDate
import scala.annotation.tailrec

def makeMax7DaysRanges(startDate: LocalDate, endDate: LocalDate): Seq[(LocalDate, LocalDate)] = {

  @tailrec
  def loop(
      start: LocalDate,
      end: LocalDate,
      acc: Seq[(LocalDate, LocalDate)]
  ): Seq[(LocalDate, LocalDate)] = {
    if start.isEqual(end) then acc
    else if end.isBefore(start.plusDays(7)) then acc :+ (start, end)
    else loop(start.plusDays(8), end, acc :+ (start, start.plusDays(7)))
  }
  loop(startDate, endDate, Seq.empty)
}
