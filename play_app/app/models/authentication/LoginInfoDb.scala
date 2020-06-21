package models.authentication


import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class LoginInfoDb(id: String,
                       providerId: String,
                       providerKey: String)

class LoginInfoTable(tag: Tag) extends Table[LoginInfoDb](tag, "login_info") {
  def id = column[String]("id", O.PrimaryKey, O.Unique)

  def providerId = column[String]("providerId")

  def providerKey = column[String]("providerKey")

  override def * = (id, providerId, providerKey) <> ((LoginInfoDb.apply _).tupled, LoginInfoDb.unapply)
}

object LoginInfoDb {
  implicit val loginInfoFormat = Json.format[LoginInfoDb]
}