package controllers.api

import javax.inject.Inject
import models.OrderItem
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.OrderItemRepository

import scala.concurrent.ExecutionContext

case class CreateOrderItem(orderId: Long, payment: Long, dispute: String, status: String)

object CreateOrderItem {
  implicit val formatOrderItem = Json.format[CreateOrderItem]
}

class OrderItemApiController @Inject()(orderItemRepository: OrderItemRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def getAll = Action.async { implicit request =>
    val order = orderItemRepository.list()
    order.map (orders => Ok(Json.toJson(orders)))
  }

  def get(id: Long) = Action.async { implicit request =>
    orderItemRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateOrderItem].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      order => {
        orderItemRepository.create(order.orderId, order.payment, order.dispute, order.status)
        Ok(Json.obj("message" -> "Order added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    orderItemRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[OrderItem].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          order => {
            orderItemRepository.update(id, OrderItem(id, order.orderId, order.payment, order.dispute, order.status))
            Ok(Json.obj("message" -> "Order upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Order not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    orderItemRepository.getByIdOption(id) map {
      case Some(c) => {
        orderItemRepository.delete(id)
        Ok(Json.obj("message" -> "Order deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Order not exist"))
    }

  }
}
