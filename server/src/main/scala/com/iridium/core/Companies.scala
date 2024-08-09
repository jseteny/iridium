package com.iridium.core

import com.iridium.domain.job.*
import com.iridium.domain.company.*
import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.*
import cats.syntax.all.*
import doobie.implicits.*
import doobie.free.connection.ConnectionIO
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor
import java.{util => ju}
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor
import doobie.util.update.Update

trait Companies[F[_]] { // "algebra"
  def create(name: String): F[UUID]
  def all: F[List[Company]]
  def idByName(name: String): F[Option[UUID]]
  def saveCompanies(companies: NonEmptyList[CompanyToDb]): F[List[CompanyFromDb]]

  def createAction(name: String): ConnectionIO[UUID]
  def allAction: ConnectionIO[List[Company]]
  def idByNameAction(name: String): ConnectionIO[Option[UUID]]
}

class CompaniesLive[F[_]: Concurrent] private (transactor: Transactor[F]) extends Companies[F] {
  override def create(name: String): F[UUID] = createAction(name).transact(transactor)

  override def all: F[List[Company]] = allAction.transact(transactor)

  override def idByName(name: String): F[Option[UUID]] = idByNameAction(name).transact(transactor)

  override def saveCompanies(companies: NonEmptyList[CompanyToDb]): F[List[CompanyFromDb]] = {
    val insertSql = "INSERT INTO companies (name) VALUES (?)"
    val action = Update[CompanyToDb](insertSql).updateManyWithGeneratedKeys[CompanyFromDb]("id", "name")(companies.toList)
    action.compile.toList.transact(transactor)
  }

  override def createAction(name: String): ConnectionIO[UUID] =
    sql"INSERT INTO companies (name) VALUES ($name) RETURNING id".update
      .withUniqueGeneratedKeys[UUID]("id")

  override def allAction: ConnectionIO[List[Company]] =
        sql"""
          SELECT c.name, array_agg(j.title) AS jobTitles, array_agg(j.id) AS jobIds
          FROM companies c
          LEFT JOIN jobs j ON c.id = j.companyId
          GROUP BY c.name
          """
            .query[Company]
            .to[List]

  override def idByNameAction(name: String): ConnectionIO[Option[UUID]] =
    sql"SELECT id FROM companies WHERE name = $name"
      .query[UUID]
      .option
}

object CompaniesLive {
  def make[F[_]: Concurrent](postgres: Transactor[F]): F[Companies[F]] = {
    val companies = new CompaniesLive[F](postgres)
    companies.pure[F]
  }
  def resource[F[_]: Concurrent](postgres: Transactor[F]): Resource[F, Companies[F]] = {
    val companies = new CompaniesLive[F](postgres)
    Resource.pure(companies)
  }
}
