package repositories

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class RatingRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){
   val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  val cust = TableQuery[CustomerTable]
   val rating = TableQuery[RatingTable]

  def create(customerId: Long, value: Int, product: Long): Future[Rating] = db.run {
    (rating.map(r => (r.customer, r.value, r.product))
      returning rating.map(_.id)

      into {case ((customer, value, product), id) => Rating(id, customer, value, product)}
      ) += (customerId, value, product)
  }

  def getById(id: Long): Future[Rating] = db.run {
    rating.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Rating]] = db.run {
    rating.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(rating.filter(_.id === id).delete).map( _ => ())

  def list(): Future[Seq[Rating]] = db.run {
    rating.result
  }

  def update(id: Long, newRating: Rating): Future[Unit] = {
    val ratToUpdate: Rating = newRating.copy(id)
    db.run(rating.filter(_.id === id).update(ratToUpdate)).map( _ => ())
  }
}
