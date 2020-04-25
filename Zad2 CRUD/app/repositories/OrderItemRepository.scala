package repositories

import javax.inject.{Inject, Singleton}
import models.OrderItem
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class OrderItemRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider, orderRepository: OrderRepository, paymentRepository: PaymentRepository)(implicit ec: ExecutionContext){
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  
  class OrderItemTable(tag: Tag) extends Table[OrderItem](tag, "orderItem") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def order_id = column[Long]("order_id")
    def payment = column[Long]("payment")
    def dispute = column[String]("dispute")
    def status = column[String]("status")
    
    def order_id_fk = foreignKey("ord_fk", order_id, ord)(_.id)
    def payment_fk = foreignKey("pay_fk", payment, pay)(_.id)
    
    def * = (id, order_id, payment, dispute, status) <> ((OrderItem.apply _).tupled, OrderItem.unapply)
  }
  import orderRepository.OrderTable
  import paymentRepository.PaymentTable
  
  private val ord = TableQuery[OrderTable]
  private val pay = TableQuery[PaymentTable]
  private val ord_it = TableQuery[OrderItemTable]

  def create(order_id: Long, payment: Long, dispute: String, status: String) = db.run {
    (ord_it.map(u => (u.order_id, u.payment, u.dispute, u.status))
      returning ord_it.map(_.id)

      into {case ((order_id, payment, dispute, status), id) => OrderItem(id, order_id, payment, dispute, status)}
      ) += (order_id,payment, dispute, status)
  }

  def getById(id: Long): Future[OrderItem] = db.run {
    ord_it.filter(_.id == id).result.head
  }

  def getByIdOption(id: Long): Future[Option[OrderItem]] = db.run {
    ord_it.filter(_.id == id).result.headOption
  }

  def list(): Future[Seq[OrderItem]] = db.run {
    ord_it.result
  }
}
