package models

import play.api.libs.json._

case class Customer(id: Long, forename: String, name: String, email: String)

object Customer {
  implicit val userFormat = Json.format[Customer]
}