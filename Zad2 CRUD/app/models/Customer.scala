package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._


case class Customer(id: Long, forename: String, name: String, email: String)

class CustomerTable(tag: Tag) extends Table[Customer](tag, "customer") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def forename = column[String]("forename")
  def name = column[String]("name")
  def email = column[String]("email")

  def * = (id, forename, name, email) <> ((Customer.apply _).tupled, Customer.unapply)

}

object Customer {
  implicit val userFormat = Json.format[Customer]
}