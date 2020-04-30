package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class OrderItem(id: Long, order_id: Long, payment: Long, dispute: String, status: String)

class OrderItemTable(tag: Tag) extends Table[OrderItem](tag, "orderItem") {
  val _ord = TableQuery[OrderTable]
  val _pay = TableQuery[PaymentTable]

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def order_id = column[Long]("order_id")

  def payment = column[Long]("payment")

  def dispute = column[String]("dispute")

  def status = column[String]("status")

  def order_id_fk = foreignKey("ord_fk", order_id, _ord)(_.id)

  def payment_fk = foreignKey("pay_fk", payment, _pay)(_.id)

  def* = (id, order_id, payment, dispute, status) <> ((OrderItem.apply _).tupled, OrderItem.unapply)
}

  object OrderItem {
  implicit val formatOrderItem = Json.format[OrderItem]
}
