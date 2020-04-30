package repositories

import java.sql.Date
import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
    val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


   val payment = TableQuery[PaymentTable]

  def create(total_price: Double, date: Date, is_done: Int): Future[Payment] = db.run {
    (payment.map(u => (u.total_price, u.date, u.is_done))
      returning payment.map(_.id)

      into { case ((total_price, date, is_done), id) => Payment(id, total_price, date, is_done) }
      ) += (total_price, date, is_done)
  }

  def getById(id: Long): Future[Payment] = db.run {
    payment.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Payment]] = db.run {
    payment.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }

  def update(id: Long, new_payment: Payment): Future[Unit] = {
    val paymentToUpdate: Payment = new_payment.copy(id)
    db.run(payment.filter(_.id === id).update(paymentToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(payment.filter(_.id === id).delete).map(_ => ())

}
