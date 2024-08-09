package com.iridium

import cats.effect.*
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import com.iridium.core.*
import com.iridium.http.*
import com.iridium.client.*
import cats.syntax.comonad
import org.http4s.server.Server

object Application extends IOApp.Simple {
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

  def makeServer: Resource[IO, Server] = for {
    postgres <- makePostgres
    companies <- CompaniesLive.resource[IO](postgres)
    jobs     <- JobsLive.resource[IO](postgres, companies)
    jobApi   <- JobRoutes.resource[IO](jobs)
    server <- EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"4041")
      .withHttpApp(CORS(jobApi.routes.orNotFound))
      .build
  } yield server

  def makeClient: Resource[IO, AsteroidClient[IO]] = for{
    client<- AsteroidClient.resource[IO]
  } yield client

  override def run: IO[Unit] =
    makeServer.both(makeClient).use { case (_,client) =>
      IO.println("Iridium! Server ready.") *>
        client.getRange("2021-09-01", "2021-09-03").flatMap(IO.println) *>
        IO.never
    }
}
