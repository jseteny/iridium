package com.iridium
package application

import cats.effect.*
import com.comcast.ip4s.*
import com.iridium.client.*
import com.iridium.http.*
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.{CORS, CORSConfig}

object Application extends IOApp.Simple {

  //noinspection ScalaDeprecation
  private val corsConfig = CORSConfig.default.withAllowCredentials(false)

  private def makePostgres = for {
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
    client<- AsteroidClient.resource[IO]
    passthroughController <- PassthroughController.resource[IO](client)
    //companies <- CompaniesLive.resource[IO](postgres)
    //jobs     <- JobsLive.resource[IO](postgres, companies)
    //jobApi   <- JobRoutes.resource[IO](jobs)
    server <- EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"4041")
      .withHttpApp(CORS(passthroughController.routes.orNotFound, corsConfig))
      .build
  } yield server

  override def run: IO[Unit] =
    makeServer.use { _ =>
      IO.println("Iridium! Server ready.") *>
        IO.never
    }
}
