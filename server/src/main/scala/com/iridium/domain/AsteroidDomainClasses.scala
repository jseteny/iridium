package com.iridium.domain

// Generated with https://transform.tools/json-to-scala-case-class

final case class Neo(
    links: Links1,
    id: Int,
    neo_reference_id: Int,
    name: String,
    nasa_jpl_url: String,
    absolute_magnitude_h: Double,
    estimated_diameter: EstimatedDiameter,
    is_potentially_hazardous_asteroid: Boolean,
    close_approach_data: Seq[CloseApproachData],
    is_sentry_object: Boolean
)

final case class CloseApproachData(
    close_approach_date: String,
    close_approach_date_full: String,
    epoch_date_close_approach: Long,
    relative_velocity: RelativeVelocity,
    miss_distance: MissDistance,
    orbiting_body: String
)

final case class EstimatedDiameter(
    kilometers: DiameterMinMax,
    meters: DiameterMinMax,
    miles: DiameterMinMax,
    feet: DiameterMinMax
)

final case class DiameterMinMax(
    estimated_diameter_min: Double,
    estimated_diameter_max: Double
)

final case class Links(
    next: String,
    previous: String,
    self: String
)

final case class Links1(
    self: String
)

final case class MissDistance(
    astronomical: Double,
    lunar: Double,
    kilometers: Double,
    miles: Double
)

final case class RelativeVelocity(
    kilometers_per_second: String,
    kilometers_per_hour: String,
    miles_per_hour: String
)

final case class AsteroidList(
    links: Option[Links],
    element_count: Option[Int],
    near_earth_objects: Option[Map[String, Seq[Neo]]],

    // Error fields
    code: Option[Int],
    http_error: Option[String],
    error_message: Option[String],
    request: Option[String]
)

final case class OrbitClass(
    orbit_class_type: String,
    orbit_class_range: String,
    orbit_class_description: String
)

final case class OrbitalData(
    orbit_id: String,
    orbit_determination_date: String,
    first_observation_date: String,
    last_observation_date: String,
    data_arc_in_days: Int,
    observations_used: Int,
    orbit_uncertainty: String,
    minimum_orbit_intersection: String,
    jupiter_tisserand_invariant: String,
    epoch_osculation: String,
    eccentricity: String,
    semi_major_axis: String,
    inclination: String,
    ascending_node_longitude: String,
    orbital_period: String,
    perihelion_distance: String,
    perihelion_argument: String,
    aphelion_distance: String,
    perihelion_time: String,
    mean_anomaly: String,
    mean_motion: String,
    equinox: String,
    orbit_class: OrbitClass
)

final case class AsteroidDetails(
    links: Links1,
    id: Int,
    neo_reference_id: String,
    name: String,
    designation: String,
    nasa_jpl_url: String,
    absolute_magnitude_h: Double,
    estimated_diameter: EstimatedDiameter,
    is_potentially_hazardous_asteroid: Boolean,
    close_approach_data: Seq[CloseApproachData],
    orbital_data: OrbitalData,
    is_sentry_object: Boolean,

// Error fields
    code: Option[Int],
    http_error: Option[String],
    error_message: Option[String],
    request: Option[String]
)
