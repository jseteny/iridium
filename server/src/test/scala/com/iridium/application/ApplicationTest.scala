package com.iridium
package application

import io.circe.literal.json
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.iridium.database.Favourites
import com.iridium.domain.Favourite
import io.circe.Json
import org.http4s.circe.CirceInstances
import org.http4s.{Request, Uri}
import org.scalactic.{Prettifier, source}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{AnyShouldWrapper, should}

class ApplicationTest extends AnyFlatSpec with Matchers with CirceInstances {

  "Application" should "respond with empty list of favourites" in {
    def initialTestData(favourites: Favourites[IO]): IO[Unit] = IO.unit
    doRequestTest(initialTestData, "/passthrough/favourites") should be(
      json"""[
      ]"""
    )
  }

  it should "respond with 2 favourites" in {
    def initialTestData(favourites: Favourites[IO]): IO[Unit] =
      favourites.create(Favourite(1, "Neo 1")) *>
        favourites.create(Favourite(2, "Neo 2")) *>
        IO.unit
    doRequestTest(initialTestData, "/passthrough/favourites") should be(
      json"""[
        {
          "id": 1,
          "name": "Neo 1"
        },
        {
          "id": 2,
          "name": "Neo 2"
        }
      ]"""
    )
  }

  it should "respond with details of asteroid id 2007822" in {
    def initialTestData(favourites: Favourites[IO]): IO[Unit] = IO.unit
    doRequestTest(initialTestData, "/passthrough/details_of/2007822").asObject
      .get("id")
      .get should be(
      json"""2007822"""
    )
  }

  it should "respond with list of asteroids" in {
    def initialTestData(favourites: Favourites[IO]): IO[Unit] = IO.unit
    doRequestTest(initialTestData, "/passthrough/search_by_range?start_date=2019-01-01&end_date=2019-01-02").asArray.get.size should be(31)
  }

  it should "respond with long list of asteroids" in {
    def initialTestData(favourites: Favourites[IO]): IO[Unit] = IO.unit
    doRequestTest(initialTestData, "/passthrough/search_by_range?start_date=2019-01-01&end_date=2019-01-10").asArray.get.size should be(180)
  }

  def doRequestTest(initialTestData: Favourites[IO] => IO[Unit], path: String): Json = {
    Application.assembleApp
      .use((controller, favourites) =>
        favourites.deleteAll *>
          initialTestData(favourites) *>
          controller.routes.orNotFound
            .run(Request[IO](uri = Uri.unsafeFromString(path)))
            .flatMap(_.as[Json])
      )
      .unsafeRunSync()
  }

}
