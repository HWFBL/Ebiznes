package models

import play.api.libs.json.Json

case class Rating(id: Long, customer_id: Long, value: Int)

object Rating {
  implicit val ratingFormat = Json.format[Rating]
}