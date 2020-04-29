package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Customer

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomerRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

    class CustomerTable(tag: Tag) extends Table[Customer](tag, "customer") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def forename = column[String]("forename")
    def name = column[String]("name")
      def email = column[String]("email")

    def * = (id, forename, name, email) <> ((Customer.apply _).tupled, Customer.unapply)

  }

  val customer = TableQuery[CustomerTable]

  def create(name: String, forename: String, email: String): Future[Customer] = db.run {
    (customer.map(u => (u.name, u.forename, u.email))
      returning customer.map(_.id)

      into {case ((name, forename, email), id) => Customer(id, name, forename, email)}
      ) += (name,forename, email)
  }

  def getByIdOption(id: Long): Future[Option[Customer]] = db.run {
    customer.filter(_.id === id).result.headOption
  }

  def getById(id: Long): Future[Customer] = db.run {
    customer.filter(_.id === id).result.head
  }

  def list(): Future[Seq[Customer]] = db.run {
    customer.result
  }

  def delete(id: Long): Future[Unit] = db.run (customer.filter(_.id === id).delete).map(_ => ())

}
