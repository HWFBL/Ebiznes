package repositories

import javax.inject.{Inject, Singleton}
import models.Order
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, customerRepository: CustomerRepository, productRepository: ProductRepository, shippingRepository: ShippingRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  
  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customer = column[Long]("customer")
    def product = column[Long]("product")
    def shipping = column[Long]("shipping")
    def quantity = column[Int]("quantity")
    
    def customer_fk = foreignKey("cust_fk", customer, cust)(_.id)
    def shipping_fk = foreignKey("ship_fk", shipping, ship)(_.id)
    def product_fk =foreignKey("prod_fk", product, prod)(_.id)
    def * = (id, customer, product, shipping, quantity) <> ((Order.apply _).tupled, Order.unapply)
  }
  
  import customerRepository.CustomerTable
  import productRepository.ProductTable
  import shippingRepository.ShippingTable
  
  private val order = TableQuery[OrderTable]
  private val cust = TableQuery[CustomerTable]
  private val prod = TableQuery[ProductTable]
  private val ship = TableQuery[ShippingTable]

  def create(customer: Long, product: Long, shipping: Long, quantity: Int): Future[Order] = db.run {
    (order.map(u => (u.customer, u.product, u.shipping, u.quantity))
      returning order.map(_.id)

      into {case ((customer, product, shipping, quantity), id) => Order(id, customer, product, shipping, quantity)}
      ) += (customer,product, shipping, quantity)
  }

  def getById(id: Long): Future[Order] = db.run {
    order.filter(_.id == id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Order]] = db.run {
    order.filter(_.id == id).result.headOption
  }

  def list() : Future[Seq[Order]] = db.run {
    order.result
  }
}
