package models.authentication

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class CustomerLoginInfo(userId: Long, loginInfoId: String)

class CustomerLoginInfoTable(tag: Tag) extends Table[CustomerLoginInfo](tag, "user_login_info") {
  def userId = column[Long]("userId")

  def loginInfoId = column[String]("loginInfoId")

  override def * = (userId, loginInfoId) <> ((CustomerLoginInfo.apply _).tupled, CustomerLoginInfo.unapply)
}

object CustomerLoginInfo {
  implicit val userLoginInfoFormat = Json.format[CustomerLoginInfo]
} 
