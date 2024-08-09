package com.iridium.core

import com.iridium.domain.job.*
import java.util.UUID

import cats.effect.*
import cats.syntax.all.*
import doobie.implicits.*
import doobie.free.connection.ConnectionIO
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor
import java.{util => ju}
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor
import com.iridium.domain.company
import cats.data.NonEmptyList
import com.iridium.domain.company.CompanyToDb

trait Jobs[F[_]] { // "algebra"
  def create(job: Job): F[UUID]
  def all: F[List[Job]]
}

class JobsLive[F[_]: Concurrent] private (transactor: Transactor[F], companies: Companies[F])
    extends Jobs[F] {
  override def all: F[List[Job]] =
    sql"""
      SELECT 
        c.name AS company,
        title,
        description,
        externalUrl,
        salaryLo,
        salaryHi,
        currency,
        remote,
        location,
        country
      FROM jobs
      JOIN companies c ON jobs.companyId = c.id
    """
      .query[Job]
      .stream
      .transact(transactor)
      .compile
      .toList

  override def create(job: Job): F[ju.UUID] =
    val insertedJob =
      for {
        companyIdOpt <- companies.idByNameAction(job.company)
        companyId <- companyIdOpt match {
          case None     => companies.createAction(job.company)
          case Some(id) => id.pure[ConnectionIO]
        }
        jobId <- sql"""
          INSERT INTO jobs (companyId, title, description, externalUrl, salaryLo, salaryHi, currency, remote, location, country)
          VALUES ($companyId, ${job.title}, ${job.description}, ${job.externalUrl}, ${job.salaryLo}, ${job.salaryHi}, ${job.currency}, ${job.remote}, ${job.location}, ${job.country})
          RETURNING id
        """.update.withUniqueGeneratedKeys[UUID]("id")
      } yield jobId

    insertedJob.transact(transactor)
}

object JobsLive {
  def make[F[_]: Concurrent](postgres: Transactor[F], companies: Companies[F]): F[JobsLive[F]] =
    new JobsLive[F](postgres, companies).pure[F]

  def resource[F[_]: Concurrent](postgres: Transactor[F], companies: Companies[F]): Resource[F, JobsLive[F]] =
    Resource.pure(new JobsLive[F](postgres, companies))
}

object JobsPlayground extends IOApp.Simple {

  def makePostgres = for {
    ec <- ExecutionContexts.fixedThreadPool[IO](32)
    transactor <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost:5432/full_stack_typelevel_demo",
      "full_stack_typelevel_demo",
      "full_stack_typelevel_demo",
      ec
    )
  } yield transactor

  def program(postgres: Transactor[IO]) =
    for {
      companies <- CompaniesLive.make[IO](postgres)
      // fromDb <- companies.saveCompanies(NonEmptyList(
      //   CompanyToDb("Rivero"), List(
      //     CompanyToDb("IBM"),
      //     CompanyToDb("Apple")
      //   )))
      // _ <- fromDb.traverse(IO.println)

      jobs <- JobsLive.make[IO](postgres, companies)
      _    <- jobs.create(Job.dummy)
      jobList <- jobs.all
      _       <- jobList.traverse(IO.println)      
      companyList <- companies.all
      _    <- companyList.traverse(IO.println)
    } yield ()

  override def run: IO[Unit] =
    makePostgres.use(program)
}
