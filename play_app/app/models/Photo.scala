package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Photo(id: Long, photo: String, link: String)

class PhotoTable(tag: Tag) extends Table[Photo](tag, "photo") {
  val _prod = TableQuery[ProductTable]

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def photo = column[String]("photo")

  def link = column[String]("link")

  def * = (id, photo, link) <> ((Photo.apply _).tupled, Photo.unapply)

}

object Photo {
  implicit val photoFormat = Json.format[Photo]
}
