package models

import play.api.libs.json.Json

case class Product(id: Long, name: String, description: String, category: Int, price: Double, quantity: Int, rating: Long, photo: Long)

object Product {
  implicit val productFormat = Json.format[Product]
}
