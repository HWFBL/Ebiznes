package models

import play.api.libs.json.Json

case class Order(id: Long, customer: Long, product: Long, shipping: Long, quantity: Int)


object Order {
  implicit val formatOrder = Json.format[Order]
}
