package com.iridium
package core

import com.iridium.domain.Neo
import io.circe.literal.json
import io.circe.Json
import io.circe.generic.auto.*
import org.http4s.circe.CirceInstances
import org.scalactic.{Prettifier, source}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{AnyShouldWrapper, should}

class CirceDecodeTest extends AnyFlatSpec with Matchers with CirceInstances {

  "Circe" should "decode a JSON with a complex structure" in {
    io.circe.parser.decode[Map[String, Seq[domain.Links1]]](
      json"""
       {
           "2015-09-08": [
            {
              "self": "http://api.nasa.gov/neo/rest/v1/neo/2465633?api_key=DEMO_KEY"
            }
          ]
       }
      """.toString
    ) should be(
      Right(
        Map(
          "2015-09-08" -> Seq(
            domain.Links1(
              "http://api.nasa.gov/neo/rest/v1/neo/2465633?api_key=DEMO_KEY"
            )
          )
        )
      )
    )
  }

  it should "decode a Map[String, Seq[Neo]]" in {
    io.circe.parser.decode[Map[String, Seq[Neo]]](
      json"""
       {
           "2015-09-08": [
            {
              "links": {
                "self": "http://api.nasa.gov/neo/rest/v1/neo/2465633?api_key=DEMO_KEY"
              },
              "id": "2465633",
              "neo_reference_id": "2465633",
              "name": "465633 (2009 JR5)",
              "nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2465633",
              "absolute_magnitude_h": 20.44,
              "estimated_diameter": {
                "kilometers": {
                  "estimated_diameter_min": 0.2170475943,
                  "estimated_diameter_max": 0.4853331752
                },
                "meters": {
                  "estimated_diameter_min": 217.0475943071,
                  "estimated_diameter_max": 485.3331752235
                },
                "miles": {
                  "estimated_diameter_min": 0.1348670807,
                  "estimated_diameter_max": 0.3015719604
                },
                "feet": {
                  "estimated_diameter_min": 712.0984293066,
                  "estimated_diameter_max": 1592.3004946003
                }
              },
              "is_potentially_hazardous_asteroid": true,
              "close_approach_data": [
                {
                  "close_approach_date": "2015-09-08",
                  "close_approach_date_full": "2015-Sep-08 20:28",
                  "epoch_date_close_approach": 1441744080000,
                  "relative_velocity": {
                    "kilometers_per_second": "18.1279360862",
                    "kilometers_per_hour": "65260.5699103704",
                    "miles_per_hour": "40550.3802312521"
                  },
                  "miss_distance": {
                    "astronomical": "0.3027469457",
                    "lunar": "117.7685618773",
                    "kilometers": "45290298.225725659",
                    "miles": "28142086.3515817342"
                  },
                  "orbiting_body": "Earth"
                }
              ],
              "is_sentry_object": false
            }
          ],

          "2015-09-07": [
            {
              "links": {
                "self": "http://api.nasa.gov/neo/rest/v1/neo/2440012?api_key=DEMO_KEY"
              },
              "id": "2440012",
              "neo_reference_id": "2440012",
              "name": "440012 (2002 LE27)",
              "nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2440012",
              "absolute_magnitude_h": 19.61,
              "estimated_diameter": {
                "kilometers": {
                  "estimated_diameter_min": 0.3180936332,
                  "estimated_diameter_max": 0.7112789871
                },
                "meters": {
                  "estimated_diameter_min": 318.0936332215,
                  "estimated_diameter_max": 711.2789870931
                },
                "miles": {
                  "estimated_diameter_min": 0.197654159,
                  "estimated_diameter_max": 0.4419681355
                },
                "feet": {
                  "estimated_diameter_min": 1043.6143156183,
                  "estimated_diameter_max": 2333.5925520145
                }
              },
              "is_potentially_hazardous_asteroid": false,
              "close_approach_data": [
                {
                  "close_approach_date": "2015-09-07",
                  "close_approach_date_full": "2015-Sep-07 07:32",
                  "epoch_date_close_approach": 1441611120000,
                  "relative_velocity": {
                    "kilometers_per_second": "1.1630843052",
                    "kilometers_per_hour": "4187.1034988155",
                    "miles_per_hour": "2601.7032823612"
                  },
                  "miss_distance": {
                    "astronomical": "0.4981690972",
                    "lunar": "193.7877788108",
                    "kilometers": "74525035.840942964",
                    "miles": "46307709.9545183432"
                  },
                  "orbiting_body": "Earth"
                }
              ],
              "is_sentry_object": false
            }
          ]
       }
      """.toString
    ) match
      case l @ Left(_) => fail(l.toString)
      case Right(_)    =>
  }

  it should "decode an Asteroid List coming from http://api.nasa.gov/neo/rest/v1" in {
    io.circe.parser.decode[domain.AsteroidList](
      scala.io.Source.fromResource("asteroids 1-3.json").mkString
    ) match
      case l @ Left(_) => fail(l.toString)
      case Right(_)    =>
  }

  it should "decode Asteroid Details coming from http://api.nasa.gov/neo/rest/v1" in {
    io.circe.parser.decode[domain.AsteroidDetails](
      scala.io.Source.fromResource("2007822.json").mkString
    ) match
      case l @ Left(_) => fail(l.toString)
      case Right(_)    =>
  }
}
