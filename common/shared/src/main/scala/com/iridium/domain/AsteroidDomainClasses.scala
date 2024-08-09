package com.iridium.domain

// Generated with https://transform.tools/json-to-scala-case-class

case class Neo(
    links: Links1,
    id: String,
    neo_reference_id: String,
    name: String,
    nasa_jpl_url: String,
    absolute_magnitude_h: Double,
    estimated_diameter: EstimatedDiameter,
    is_potentially_hazardous_asteroid: Boolean,
    close_approach_data: Seq[CloseApproachData],
    is_sentry_object: Boolean
)

case class CloseApproachData(
    close_approach_date: String,
    close_approach_date_full: String,
    epoch_date_close_approach: Long,
    relative_velocity: RelativeVelocity,
    miss_distance: MissDistance,
    orbiting_body: String
)

case class EstimatedDiameter(
    kilometers: Kilometers,
    meters: Kilometers,
    miles: Kilometers,
    feet: Kilometers
)

case class Kilometers(
    estimated_diameter_min: Double,
    estimated_diameter_max: Double
)

case class Links(
    next: String,
    previous: String,
    self: String
)

case class Links1(
    self: String
)

case class MissDistance(
    astronomical: String,
    lunar: String,
    kilometers: String,
    miles: String
)

case class RelativeVelocity(
    kilometers_per_second: String,
    kilometers_per_hour: String,
    miles_per_hour: String
)

case class RootInterface(
    links: Option[Links],
    element_count: Option[Int],
    near_earth_objects: Option[Map[String, Seq[Neo]]],

    // ErrorRootInterface
    code: Option[Int],
    http_error: Option[String],
    error_message: Option[String],
    request: Option[String]
)
