package com.iridium.domain

import java.util.UUID

object company {
  case class Company(
      name: String,
      jobTitles: List[Option[String]],
      jobIds: List[Option[UUID]]
  )
  case class CompanyToDb(
    name:String
  )
  case class CompanyFromDb(
    id:UUID,
    name:String
  )
}   