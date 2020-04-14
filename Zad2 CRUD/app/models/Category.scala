package models

import play.api.libs.json.Json

case class Category(id: Int, name: String)

object Category {
  implicit val categoryFormat = Json.format[Category]
}