package repositories

import javax.inject.{Inject, Singleton}
import models.Rating
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class RatingRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, customerRepository: CustomerRepository)(implicit ec: ExecutionContext){
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class RatingTable(tag: Tag) extends Table[Rating](tag, "rating") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customer_id = column[Long]("customer_id")
    def value = column[Int]("value")
    def customer_id_fk = foreignKey("cust_fk", customer_id, cust)

    def * = (id, customer_id, value) <> ((Rating.apply _).tupled, Rating.unapply)
  }

  import customerRepository.CustomerTable

  private val cust = TableQuery[CustomerTable]
  private val rating = TableQuery[RatingTable]

  def create(customer_id: Long, value: Int): Future[Rating] = db.run {
    (rating.map(r => (r.customer_id, r.value))
      returning rating.map(_.id)

      into {case ((customer_id, value), id) => Rating(id, customer_id, value)}
      ) += (customer_id, value)
  }

  def getById(id: Long): Future[Rating] = db.run {
    rating.filter(_.id == id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Rating]] = db.run {
    rating.filter(_.id == id).result.headOption
  }

  def list(): Future[Seq[Rating]] = db.run {
    rating.result
  }
}
