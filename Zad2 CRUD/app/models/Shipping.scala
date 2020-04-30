package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Shipping(id: Long, street: String, house_number: String, city: String, zip_code: String)

class ShippingTable(tag: Tag) extends Table[Shipping](tag, "shipping") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def street = column[String]("street")
  def house_number = column[String]("house_number")
  def city = column[String]("city")
  def zip_code = column[String]("zip_code")

  def * = (id, street, house_number, city, zip_code) <> ((Shipping.apply _).tupled, Shipping.unapply)
}

object Shipping {
  implicit val formatShipping = Json.format[Shipping]
}
