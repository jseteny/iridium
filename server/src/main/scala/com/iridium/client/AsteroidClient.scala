package com.iridium.client


import cats.*
import cats.effect.*
import com.iridium.domain.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.ember.client.*
import org.http4s.implicits.*

class AsteroidClient[F[_] : Async] {

  def getRange(from: String, to: String): F[RootInterface] =
    EmberClientBuilder.default[F].build.use { client =>
      val uri = uri"https://api.nasa.gov/neo/rest/v1/feed"
        .+?("start_date" -> from)
        .+?("end_date" -> to)
        .+?("api_key" -> "DEMO_KEY")
      val request = Request[F](Method.GET, uri)
      client.run(request).use {
        case Status.Successful(response) => response.as[RootInterface]
        case r => r.as[RootInterface]/*.map(body =>
        //  s"Request $request failed with status ${r.status.code} and body $body"
          //RootInterface(null,0,null)
          ErrorRootInterface(999, http_error =s"Request $request failed with status ${r.status.code} and body $body", null, null)
        )*/
      }
    }
}

object AsteroidClient {
  def resource[F[_] : Async]: Resource[F, AsteroidClient[F]] =
    Resource.pure(new AsteroidClient[F])
}
