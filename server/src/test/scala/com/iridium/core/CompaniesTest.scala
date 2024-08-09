package com.iridium.core

import org.scalatest.funspec.AnyFunSpec
import doobie.util.transactor.Transactor
import cats.effect.IO

class CompaniesTest extends AnyFunSpec with doobie.scalatest.IOChecker{

  override def transactor = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost:5432/full_stack_typelevel_demo",
      "full_stack_typelevel_demo",
      "full_stack_typelevel_demo",
    )

    val companies = CompaniesLive.make[IO](transactor)

    describe("all should"){
        it("have a valid SQL"){
            fail("Not Implemented")
        }
    }
}