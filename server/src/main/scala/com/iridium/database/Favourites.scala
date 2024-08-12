package com.iridium.database

import cats.effect.*
import cats.syntax.all.*
import com.iridium.domain.*
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

trait Favourites[F[_]] { // "algebra"
  def create(favourite: Favourite): F[Int]
  def all: F[List[Favourite]]
  def exists(id: Int): F[Boolean]
  def deleteAll: F[Unit]
}

class FavouritesLive[F[_]: Concurrent] private (transactor: Transactor[F]) extends Favourites[F] {
  override def all: F[List[Favourite]] =
    sql"""
      SELECT
        id,
        name
      FROM favourites
    """
      .query[Favourite]
      .stream
      .transact(transactor)
      .compile
      .toList

  override def exists(id: Int): F[Boolean] =
    sql"""
      SELECT
        id,
        name
      FROM favourites
      WHERE id = $id
    """
      .query[Favourite]
      .stream
      .transact(transactor)
      .compile
      .count
      .map(_ > 0)

  override def create(favourite: Favourite): F[Int] = {
    val insertedFavourite =
      for {
        favouriteId <- sql"""
          INSERT INTO favourites (id, name)
          VALUES (${favourite.id}, ${favourite.name})
          RETURNING id
        """.update.withUniqueGeneratedKeys[Int]("id")
      } yield favouriteId

    insertedFavourite.transact(transactor)
  }

  override def deleteAll: F[Unit] = {
    sql"""
      DELETE FROM favourites
      """.update.run.transact(transactor).void
  }
}

object FavouritesLive {
  def make[F[_]: Concurrent](postgres: Transactor[F]): F[FavouritesLive[F]] =
    new FavouritesLive[F](postgres).pure[F]

  def resource[F[_]: Concurrent](postgres: Transactor[F]): Resource[F, FavouritesLive[F]] =
    Resource.pure(new FavouritesLive[F](postgres))
}

object FavouritesPlayground extends IOApp.Simple {

  private def makePostgres = for {
    config <- com.iridium.application.Application.loadConfig("config.txt")
    ec <- ExecutionContexts.fixedThreadPool[IO](32)
    transactor <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      config.dbDatabaseJdbcUri,
      config.dbUser,
      config.dbPassword,
      ec
    )
  } yield transactor

  private def program(postgres: Transactor[IO]) =
    for {
      favourites <- FavouritesLive.make[IO](postgres)
      favouriteList <- favourites.all
      _    <- favouriteList.traverse(IO.println)
    } yield ()

  override def run: IO[Unit] =
    makePostgres.use(program)
}
