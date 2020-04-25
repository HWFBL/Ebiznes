package models

import play.api.libs.json.Json

case class Photo(id: Long, photo: Array[Byte])

object Photo {
  implicit val photoFormat = Json.format[Photo]
}
