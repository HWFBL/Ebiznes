package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Rating(id: Long, customer_id: Long, value: Int, product: Long)

class RatingTable(tag: Tag) extends Table[Rating](tag, "rating") {

  val _cust = TableQuery[CustomerTable]
  val _prod = TableQuery[ProductTable]

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def customer = column[Long]("customer_id")
  def value = column[Int]("value")
  def product = column[Long]("product")
  def customer_fk = foreignKey("custo_fk", customer, _cust)(_.id)
  def product_fk = foreignKey("prod_fk", product, _prod)(_.id)

  def * = (id, customer, value, product) <> ((Rating.apply _).tupled, Rating.unapply)
}

object Rating {
  implicit val ratingFormat = Json.format[Rating]
}