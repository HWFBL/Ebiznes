package models

import play.api.libs.json.Json

case class OrderItem(id: Long, order_id: Long, payment: Long, dispute: String, status: String)

object OrderItem {
  implicit val formatOrderItem = Json.format[OrderItem]
}
