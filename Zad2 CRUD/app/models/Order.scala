package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Order(id: Long, customer: Long, product: Long, shipping: Long, quantity: Int)

class OrderTable(tag: Tag) extends Table[Order](tag, "order") {

  val _cust = TableQuery[CustomerTable]
  val _prod = TableQuery[ProductTable]
  val _ship = TableQuery[ShippingTable]
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def customer = column[Long]("customer")
  def product = column[Long]("product")
  def shipping = column[Long]("shipping")
  def quantity = column[Int]("quantity")

  def customer_fk = foreignKey("cust_fk", customer, _cust)(_.id)
  def shipping_fk = foreignKey("ship_fk", shipping, _ship)(_.id)
  def product_fk =foreignKey("prod_fk", product, _prod)(_.id)
  def * = (id, customer, product, shipping, quantity) <> ((Order.apply _).tupled, Order.unapply)
}

object Order {
  implicit val formatOrder = Json.format[Order]
}
