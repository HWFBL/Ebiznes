package repositories

import akka.pattern.FutureRef
import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, customerRepository: CustomerRepository, productRepository: ProductRepository, shippingRepository: ShippingRepository)(implicit ec: ExecutionContext) {
   val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  
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

  def delete(id: Long ): Future[Unit] = db.run(order.filter(_.id === id).delete).map( _ => ())

  def update(id: Long, newOrder: Order) = {
    val ordToUpdate: Order = newOrder.copy(id)
    db.run(order.filter(_.id === id).update(ordToUpdate)).map( _ => ())
  }

  def getById(id: Long): Future[Order] = db.run {
    order.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Order]] = db.run {
    order.filter(_.id === id).result.headOption
  }

  def list() : Future[Seq[Order]] = db.run {
    order.result
  }
}
