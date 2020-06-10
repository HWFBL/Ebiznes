package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Shipping(id: Long, street: String, houseNumber: String, city: String, zipCode: String)

class ShippingTable(tag: Tag) extends Table[Shipping](tag, "shipping") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def street = column[String]("street")
  def houseNumber = column[String]("houseNumber")
  def city = column[String]("city")
  def zipCode = column[String]("zipCode")

  def * = (id, street, houseNumber, city, zipCode) <> ((Shipping.apply _).tupled, Shipping.unapply)
}

object Shipping {
  implicit val formatShipping = Json.format[Shipping]
}
