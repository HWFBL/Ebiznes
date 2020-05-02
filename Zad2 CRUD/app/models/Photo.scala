package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Photo(id: Long, photo: String, product: Long)

class PhotoTable(tag: Tag) extends Table[Photo](tag, "photo") {
  val _prod = TableQuery[ProductTable]

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def photo = column[String]("photo")

  def product = column[Long]("product")

  def product_fk = foreignKey("prod_fk", product, _prod)(_.id)

  def * = (id, photo, product) <> ((Photo.apply _).tupled, Photo.unapply)

}

object Photo {
  implicit val photoFormat = Json.format[Photo]
}
