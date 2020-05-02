package controllers

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{OrderItemRepository, OrderRepository, PaymentRepository}
import models._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class OrderItemController @Inject()(orderItemRepository: OrderItemRepository, orderRepository: OrderRepository, paymentRepository: PaymentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

  val orderItemForm: Form[OrderItemForm] = Form {
    mapping(
   "order" -> longNumber,
      "payment" -> longNumber,
      "dispute" -> nonEmptyText,
      "status" -> nonEmptyText

    )(OrderItemForm.apply)(OrderItemForm.unapply)
  }

  val updateorderItemForm: Form[UpdateOrderItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "order" -> longNumber,
      "payment" -> longNumber,
      "dispute" -> nonEmptyText,
      "status" -> nonEmptyText

    )(UpdateOrderItemForm.apply)(UpdateOrderItemForm.unapply)
  }


  def add = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val order: Seq[Order] = Await.result(orderRepository.list(), Duration.Inf)
    val pay: Seq[Payment] = Await.result(paymentRepository.list(), Duration.Inf)

    Future.successful(Ok(views.html.orderItem.addorderitem(orderItemForm, order, pay)))
  }

  def addHandle = Action.async { implicit request =>
    var ord: Seq[Order] = Seq[Order]()
    val orders = orderRepository.list().onComplete {
      case Success(cat) => ord = cat
      case Failure(_) => print("fail")

    }

    var pay: Seq[Payment] = Seq[Payment]()
    val payments = paymentRepository.list().onComplete {
      case Success(c) => pay = c
      case Failure(_) => print("fail")
    }



    orderItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderItem.addorderitem(errorForm, ord, pay))
        )
      },
      ord => {
        orderItemRepository.create(ord.order, ord.payment, ord.dispute, ord.status).map { _ =>
          Redirect(routes.OrderItemController.add()).flashing("success" -> "order added")
        }
      }
    )

  }

  def delete(ratingId: Long) = Action {
    orderItemRepository.delete(ratingId)
    Redirect("/orderitems")
  }

  def update(id: Long) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var ord: Seq[Order] = Seq[Order]()
    val orders = orderRepository.list().onComplete {
      case Success(cat) => ord = cat
      case Failure(_) => print("fail")

    }

    var pay: Seq[Payment] = Seq[Payment]()
    val payments = paymentRepository.list().onComplete {
      case Success(c) => pay = c
      case Failure(_) => print("fail")
    }



    val orderItem = orderItemRepository.getById(id)
    orderItem.map(o => {
      val oForm = updateorderItemForm.fill(UpdateOrderItemForm(o.id, o.order_id, o.payment, o.dispute, o.status))
      //  id, product.name, product.description, product.category)
      //updateProductForm.fill(prodForm)
      Ok(views.html.orderItem.updateorderitem(oForm, ord, pay))
    })
  }

  def updateHandle = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var ord: Seq[Order] = Seq[Order]()
    val orders = orderRepository.list().onComplete {
      case Success(cat) => ord = cat
      case Failure(_) => print("fail")

    }

    var pay: Seq[Payment] = Seq[Payment]()
    val payments = paymentRepository.list().onComplete {
      case Success(c) => pay = c
      case Failure(_) => print("fail")
    }


    updateorderItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderItem.updateorderitem(errorForm, ord, pay))
        )
      },
      o => {
        orderItemRepository.update(o.id, OrderItem(o.id, o.order, o.payment, o.dispute, o.status)).map { _ =>
          Redirect(routes.OrderItemController.add()).flashing("success" -> "rating added")
        }
      }
    )
  }

  def getAll = Action.async {
    implicit request =>
      val ord_it = orderItemRepository.list()
      ord_it.map(orders => Ok(views.html.orderItem.orderItems(orders)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val ord_it = orderItemRepository.getByIdOption(id)
    ord_it.map(order => order match {
      case Some(c) => Ok(views.html.orderItem.orderItem(c))
      case None => Redirect(routes.OrderItemController.getAll)
    })
  }
}

case class OrderItemForm(order: Long, payment: Long, dispute: String, status: String)
case class UpdateOrderItemForm(id: Long, order: Long, payment: Long, dispute: String, status: String)