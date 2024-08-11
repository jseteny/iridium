package com.iridium
package http

import com.iridium.client.AsteroidClient
import cats.effect.*
import cats.*
import cats.syntax.all.*
import com.iridium.domain.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.server.Router
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.util.Try
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PassthroughController[F[_]: Async] private (
    client: AsteroidClient[F]
) extends Http4sDsl[F] {

  private val prefix = "/passthrough"

  private val logger = Slf4jLogger.getLogger[F]

  implicit val localDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder[String].emap { str =>
      Try(LocalDate.parse(str, DateTimeFormatter.ISO_DATE)).toEither
        .leftMap(t => ParseFailure("Invalid date format", t.getMessage))
    }

  private object StartDateQueryParamMatcher
      extends QueryParamDecoderMatcher[LocalDate]("start_date")
  private object EndDateQueryParamMatcher extends QueryParamDecoderMatcher[LocalDate]("end_date")

  // get /passthrough/search_by_range/
  private val searchByRange: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "search_by_range"
        :? StartDateQueryParamMatcher(startDate)
        +& EndDateQueryParamMatcher(endDate) =>
      for {
        asteroidList <- client.searchByRange(startDate, endDate)
        asteroidNames = asteroidList.near_earth_objects.map { neosByDate =>
          neosByDate
            .flatMap((date, neos) =>
              neos.map { neo =>
                AsteroidOutputList(name = neo.name, id = neo.id)
              }
            )
            .toList
            .sortBy(_.name)
        }
        response <- asteroidNames
          .map(Ok(_))
          .getOrElse(
            InternalServerError(
              s"Got ${asteroidList.code} ${asteroidList.http_error}: ${asteroidList.error_message} for the request ${asteroidList.request}"
            )
          )
      } yield response
  }

  // get /passthrough/search_by_range/
  private val searchByRange7Inactive: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "search_by_range"
        :? StartDateQueryParamMatcher(startDate)
        +& EndDateQueryParamMatcher(endDate) =>
      for {
        ranges <- Monad[F].pure(logic.makeMax7DaysRanges(startDate, endDate))
        _      <- logger.info(s"ranges: $ranges")
        response <- ranges.foldLeft(Ok(Some(Seq.empty[AsteroidOutputList]))) {
          case (Some(asteroidsSoFar: Seq[AsteroidOutputList]), (startDate, endDate)) =>
            for {
              _            <- logger.info(s"inside for loop: $startDate, $endDate")
              asteroidList <- client.searchByRange(startDate, endDate)
              asteroidsMaybe = asteroidList.near_earth_objects.map { neosByDate =>
                neosByDate
                  .flatMap((_, neos) =>
                    neos.map { neo =>
                      AsteroidOutputList(name = neo.name, id = neo.id)
                    }
                  )
                  .toList
              }
              response <- asteroidsMaybe
                .map(asteroids => Ok((asteroidsSoFar ++ asteroids).sortBy(_.name)))
                .getOrElse(Ok(None)) // I will convert it to InternalServerError later
            } yield response
          case (None, _) => Ok(None)
        }
      } yield response
  }

// get /passthrough/detailsOf/{asteroidId}
  private val detailsOf: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "details_of" / IntVar(asteroidId) =>
      for {
        asteroidDetails <- client.detailsOf(asteroidId)
        response        <- Ok(asteroidDetails)
      } yield response
  }

  val routes: HttpRoutes[F] = Router(
    prefix -> (searchByRange <+> detailsOf)
  )
}

object PassthroughController {
  def resource[F[_]: Async](
      client: AsteroidClient[F]
  ): Resource[F, PassthroughController[F]] =
    Resource.pure(new PassthroughController[F](client))
}
