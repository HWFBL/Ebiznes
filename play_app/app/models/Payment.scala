package models

import java.sql.Date

import slick.jdbc.SQLiteProfile.api._
import java.time.LocalDate

import play.api.libs.json.Json

case class Payment(id: Long, totalPrice: Double, date: Date,isDone: Int )

class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def totalPrice = column[Double]("total_price")

  def date = column[Date]("date")

  def isDone = column[Int]("is_done")

  def * = (id, totalPrice, date, isDone) <> ((Payment.apply _).tupled, Payment.unapply)
}


object Payment {
  implicit val formatPayment = Json.format[Payment]
}
