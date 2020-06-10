package repositories

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class OrderItemRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider, orderRepository: OrderRepository, paymentRepository: PaymentRepository)(implicit ec: ExecutionContext){
   val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


      val ord = TableQuery[OrderTable]
      val pay = TableQuery[PaymentTable]
     val ord_it = TableQuery[OrderItemTable]

     def create(orderId: Long, payment: Long, dispute: String, status: String) = db.run {
       (ord_it.map(u => (u.orderId, u.payment, u.dispute, u.status))
         returning ord_it.map(_.id)

         into { case ((orderId, payment, dispute, status), id) => OrderItem(id, orderId, payment, dispute, status) }
         ) += (orderId, payment, dispute, status)
     }

     def getById(id: Long): Future[OrderItem] = db.run {
       ord_it.filter(_.id === id).result.head
     }

     def getByIdOption(id: Long): Future[Option[OrderItem]] = db.run {
       ord_it.filter(_.id === id).result.headOption
     }

     def list(): Future[Seq[OrderItem]] = db.run {
       ord_it.result
     }
  def delete(id: Long): Future[Unit] = db.run(ord_it.filter(_.id === id).delete).map( _ => ())

  def update(id: Long, newOrderItem: OrderItem): Future[Unit] = {
    val oitToUpdate: OrderItem = newOrderItem.copy(id)
    db.run(ord_it.filter(_.id === id).update(oitToUpdate)).map(_ => ())
  }
}
