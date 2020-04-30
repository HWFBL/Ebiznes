package models

import java.sql.Date

import slick.jdbc.SQLiteProfile.api._
import java.time.LocalDate

import play.api.libs.json.Json

case class Payment(id: Long, total_price: Double, date: Date,is_done: Int )

class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def total_price = column[Double]("total_price")

  def date = column[Date]("date")

  def is_done = column[Int]("is_done")

  def * = (id, total_price, date, is_done) <> ((Payment.apply _).tupled, Payment.unapply)
}


object Payment {
  implicit val formatPayment = Json.format[Payment]
}
