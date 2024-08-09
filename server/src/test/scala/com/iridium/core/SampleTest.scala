package com.iridium
package core

import io.circe.literal.json
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.Json
import org.http4s.circe.CirceInstances
import org.http4s.{Request, Uri}
import org.scalactic.{Prettifier, source}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.AnyShouldWrapper

implicit def convertToAnyShouldWrapper[T](o: T)(implicit pos: source.Position, prettifier: Prettifier): AnyShouldWrapper[T] = new AnyShouldWrapper(o, pos, prettifier)

/** This is actually an integration test file for the sake of simplicity
  */
class SampleTest extends AnyFlatSpec with Matchers with CirceInstances {
/*
  "Application" should "respond with hello world" in {
    def initialTestData(databaseTables: DatabaseTables): IO[Unit] = IO.unit
    doRequestTest(initialTestData)("/") should be(
      json""""Hello World!""""
    )
  }

  it should "authorization with 1 clearing" in {
    def initialTestData(databaseTables: DatabaseTables): IO[Unit] = {
      databaseTables.authorizationTable.insert(
        Authorization(1, BigDecimal(42), "gbp")
      ) *>
        databaseTables.clearingTable.insert(
          Clearing(2, 1, BigDecimal(42), "Merchant 1")
        )
    }
    doRequestTest(initialTestData)("/transactions") should be(
      json"""[
        {
          "authorization": 
            {
              "id": 1,
              "amount": 42,
              "currency": "gbp"
            },
          "clearings": [
            { "id": 2, "authorization_id" : 1, "amount": 42, "merchant": "Merchant 1"}  
          ]
        }
      ]"""
    )
  }

  def doRequestTest(
      initialTestData: DatabaseTables => IO[Unit]
  )(path: String): Json = {
    Application
      .assembleApp(initialTestData)
      .use(controller =>
        controller.router
          .run(Request[IO](uri = Uri.unsafeFromString(path)))
          .flatMap(_.as[Json])
      )
      .unsafeRunSync()
  }
  
 */
}
