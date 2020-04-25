package models

import play.api.libs.json.Json

case class Shipping(id: Long, street: String, house_number: String, city: String, zip_code: String)

object Shipping {
  implicit val formatShipping = Json.format[Shipping]
}
