package com.iridium.client

import com.iridium.domain.*
import cats.*
import cats.effect.*
import cats.implicits.{toFlatMapOps, toFunctorOps}
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.ember.client.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

class AsteroidClient[F[_]: Async] private (config: AsteroidClient.Config) {

  private val threadPoolOf32 = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))
  private val logger         = Slf4jLogger.getLogger[F]

  private implicit val localDateQueryParamEncoder: QueryParamEncoder[LocalDate] =
    QueryParamEncoder[String].contramap(DateTimeFormatter.ISO_LOCAL_DATE.format)

  def searchByRange(from: LocalDate, to: LocalDate): F[AsteroidList] =
    EmberClientBuilder.default[F].build.use { client =>
      val uri = uri"https://api.nasa.gov/neo/rest/v1/feed"
        .+?("start_date" -> from)
        .+?("end_date" -> to)
        .+?("api_key" -> config.apiKey)
      val request = Request[F](Method.GET, uri)
      client.run(request).evalOn(threadPoolOf32).use {
        case Status.Successful(response) => response.as[AsteroidList]
        case r                           => r.as[AsteroidList]
      }
    }
  
  def detailsOf(asteroidId: Int): F[AsteroidDetails] =
    EmberClientBuilder.default[F].build.use { client =>
      val uri = uri"https://api.nasa.gov/neo/rest/v1/neo"
        .addSegment(asteroidId.toString)
        .+?("api_key" -> config.apiKey)
      val request = Request[F](Method.GET, uri)
      client.run(request).evalOn(threadPoolOf32).use {
        case Status.Successful(response) => response.as[AsteroidDetails]
        case r =>
          for {
            _      <- logger.error(s"Failed to fetch details of asteroid $asteroidId. Response: $r")
            result <- r.as[AsteroidDetails]
          } yield result
      }
    }
}

object AsteroidClient {
  final case class Config(apiKey: String)

  def resource[F[_]: Async](config: Config): Resource[F, AsteroidClient[F]] =
    Resource.pure(new AsteroidClient[F](config))
}
