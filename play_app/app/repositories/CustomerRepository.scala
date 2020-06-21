package repositories


import java.util.UUID

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.authentication.LoginInfoTable
import repositories.authentication.{CustomerDAO, LoginInfoDAO}

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class CustomerRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, passwordHasher: PasswordHasher, authInfoRepository: AuthInfoRepository, customerDAO: CustomerDAO, loginInfoDAO: LoginInfoDAO )(implicit ec: ExecutionContext) extends IdentityService[Customer] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  val customer = TableQuery[CustomerTable]
  val password = TableQuery[PasswordTable]
  val loginInfo = TableQuery[LoginInfoTable]


  override def retrieve(loginInfo: LoginInfo) = {
    customerDAO.find(loginInfo)
  }

  def saveOrUpdate(forename: String, name: String, email: String, loginInfo: LoginInfo) = {
    customerDAO.find(loginInfo).flatMap {
      case Some(foundUser) => customerDAO.update(foundUser.id, forename, name, email, foundUser.roles)
      case None => save(forename, name, email, loginInfo)
    }
  }

  def save(forename: String, name: String, email: String, loginInfo: LoginInfo) = {
    for {
      savedUserId <- customerDAO.save(forename, name, email)
      _ <- loginInfoDAO.saveUserLoginInfo(savedUserId, loginInfo)
    } yield savedUserId
  }


//  def create(name: String, forename: String, email: String, password: String) = {
//    val id = UUID.randomUUID().getMostSignificantBits() & Long.MaxValue
//    db.run {
//
//   customer.insertOrUpdate(Customer(id, forename, name, email, email))
//
//    }  andThen {
//      case Failure(_: Throwable) => None
//      case Success(_) => {
//        val loginInfo: LoginInfo = LoginInfo(CredentialsProvider.ID, email)
//        val authInfo: PasswordInfo = passwordHasher.hash(password)
//        authInfoRepository.add(loginInfo, authInfo)
//      }
//    } map { _id => LoginInfo(CredentialsProvider.ID, email) }
//  }

  def getByIdOption(id: Long): Future[Option[Customer]] = db.run {
    customer.filter(_.id === id).result.headOption
  }

  def getByEmail(email: String): Future[Option[Customer]] = db.run {
    customer.filter(_.email === email).result.headOption
  }

  def getById(id: Long): Future[Customer] = db.run {
    customer.filter(_.id === id).result.head
  }

  def list(): Future[Seq[Customer]] = db.run {
    customer.result
  }

  def delete(id: Long): Future[Unit] = db.run {
  val cust: Customer = Await.result(getByIdOption(id), Duration.Inf).get
    customer.filter(_.id === id).delete.map(_ => ())
  }

  def update(id: Long, newCust: Customer): Future[Unit] = {
    val custToUpdate: Customer = newCust.copy(id)
    db.run(customer.filter(_.id === id).update(custToUpdate)).map(_ => ())
  }

}
