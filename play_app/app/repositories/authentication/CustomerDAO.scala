package repositories.authentication

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{Inject, Singleton}
import models._
import models.authentication.{CustomerLoginInfoTable, LoginInfoTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class CustomerDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  val customer = TableQuery[CustomerTable]
  val _loginInfo = TableQuery[LoginInfoTable]
  val customerLoginInfo = TableQuery[CustomerLoginInfoTable]

  def save(forename: String, name: String, email: String) = db.run {
    val id: Long = UUID.randomUUID().getMostSignificantBits() & Long.MaxValue
    customer.insertOrUpdate(Customer(id, forename, name, email, UserRoles.Customer)).map(_ => id)
  }

  def update(id: Long, forename: String, name: String, email: String, role: UserRoles.UserRole) = db.run {
    customer.filter(_.id === id).update(Customer(id, forename, name, email, role))
  }

  def find(loginInfo: LoginInfo) = {
    val findLoginInfoQuery = _loginInfo.filter(dbLoginInfo =>
      dbLoginInfo.providerId === loginInfo.providerID &&  dbLoginInfo.providerKey === loginInfo.providerKey)
    val query = for {
      dbLoginInfo <- findLoginInfoQuery
      dbCustomerLoginInfo <- customerLoginInfo.filter(_.loginInfoId === dbLoginInfo.id)
      dbCustomer <- customer.filter(_.id === dbCustomerLoginInfo.userId)
    } yield dbCustomer
    db.run(query.result.headOption).map { dbCustomerOption =>
      dbCustomerOption.map { user =>
        Customer(user.id, user.forename, user.name, user.email, user.roles)
      }
    }
  }

} 