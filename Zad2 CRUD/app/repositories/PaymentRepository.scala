package repositories

import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def total_price = column[Double]("total_price")

    def date = column[LocalDate]("date")

    def is_done = column[Int]("is_done")

    def * = (id, total_price, date, is_done) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  private val payment = TableQuery[PaymentTable]

  def create(total_price: Double, date: LocalDate, is_done: Int) = db.run {
    (payment.map(u => (u.total_price, u.date, u.is_done, ))
      returning payment.map(_.id)

      into { case ((total_price, date, is_done, ), id) => Payment(id, total_price, date, is_done) }
      ) += (total_price, date, is_done)
  }

  def getById(id: Long): Future[Payment] = db.run {
    payment.filter(_.id == id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id == id).result.headOption
  }

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }
}
