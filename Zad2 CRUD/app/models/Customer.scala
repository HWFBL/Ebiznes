package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._
import com.mohiva.play.silhouette.api.Identity


object UserRoles extends Enumeration {
  type UserRole = String
  val Customer = "customer"
  val Admin = "admin"
}

case class Customer(id: Long, forename: String, name: String, email: String, roles: UserRoles.UserRole, providerId: String, providerKey: String) extends Identity

class CustomerTable(tag: Tag) extends Table[Customer](tag, "customer") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def forename = column[String]("forename")
  def name = column[String]("name")
  def email = column[String]("email")
  def role = column[String]("role")
  def providerId = column[String]("providerId")
  def providerKey = column[String]("providerKey")

  def * = (id, forename, name, email, role, providerId, providerKey) <> ((Customer.apply _).tupled, Customer.unapply)

}

case class Password(key: String,
                    hasher: String,
                    hash: String,
                    salt: Option[String])


class PasswordTable(tag: Tag) extends Table[Password](tag, "password") {
  def key = column[String]("providerKey", O.PrimaryKey)

  def hasher = column[String]("hasher")

  def hash = column[String]("hash")

  def salt = column[Option[String]]("salt")

  def * = (key, hasher, hash, salt) <> ((Password.apply _).tupled, Password.unapply)
}

object Password {
  implicit val passwordFormat = Json.format[Password]
}

object Customer {
  implicit val userFormat = Json.format[Customer]
}