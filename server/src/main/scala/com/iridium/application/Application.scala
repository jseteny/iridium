package com.iridium
package application

import cats.effect.*
import com.comcast.ip4s.*
import com.iridium.client.*
import com.iridium.database.*
import com.iridium.http.*
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.{CORS, CORSConfig}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.*
import pureconfig.generic.derivation.default.*

object Application extends IOApp.Simple {

  final case class Config(dbDatabaseJdbcUri: String, dbUser: String, dbPassword: String, clientConfig: AsteroidClient.Config)
    derives ConfigReader

  // noinspection ScalaDeprecation
  private val corsConfig = CORSConfig.default.withAllowCredentials(false)
  private val logger     = Slf4jLogger.getLogger[IO]

  def loadConfig(fileName: String): Resource[IO, Config] = {
    Resource.pure(
      ConfigSource
        .file(fileName)
        .load[Config]
        .fold(
          e => throw new RuntimeException(e.toString),
          identity
        )
    )
  }

  private def makePostgres(config: Config) = for {
    ec <- ExecutionContexts.fixedThreadPool[IO](32)
    transactor <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      config.dbDatabaseJdbcUri,
      config.dbUser,
      config.dbPassword,
      ec
    )
  } yield transactor

  def assembleApp: Resource[IO, (PassthroughController[IO], Favourites[IO])] = {
    for {
      config                <- loadConfig("config.txt")
      postgres              <- makePostgres(config)
      client                <- AsteroidClient.resource[IO](config.clientConfig)
      favourites            <- FavouritesLive.resource[IO](postgres)
      passthroughController <- PassthroughController.resource[IO](client, favourites)
    } yield (passthroughController, favourites)
  }

  def makeServer: Resource[IO, Server] = for {
    tuple <- assembleApp
    (passthroughController, _) = tuple
    server <- EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"4041")
      .withHttpApp(CORS(passthroughController.routes.orNotFound, corsConfig))
      .build
  } yield server

  override def run: IO[Unit] =
    makeServer.use { _ =>
      logger.info("Iridium Server ready.") *>
        IO.never
    }
}
