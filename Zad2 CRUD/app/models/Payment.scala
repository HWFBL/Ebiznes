package models

import java.time.LocalDate

import play.api.libs.json.Json

case class Payment(id: Long, total_price: Double, date: LocalDate,is_done: Int )

object Payment {
  implicit val formatPayment = Json.format[Payment]
}
