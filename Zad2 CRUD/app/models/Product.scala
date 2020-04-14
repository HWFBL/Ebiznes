package models

import play.api.libs.json.Json

case class Product(id: Long, title: String, category: Int, description: String)

object Product {
  implicit val productFormat = Json.format[Product]
}
