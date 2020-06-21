package controllers.api

import javax.inject.Inject
import models.Order
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.OrderRepository

import scala.concurrent.ExecutionContext

case class CreateOrder(customer: Long, product: Long, shipping: Long, quantity: Int)

object CreateOrder {
  implicit val formatOrder = Json.format[CreateOrder]
}

class OrderApiController @Inject()(orderRepository: OrderRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def getAll = Action.async { implicit request =>
    val order = orderRepository.list()
    order.map (orders => Ok(Json.toJson(orders)))
  }

  def get(id: Long) = Action.async { implicit request =>
    orderRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateOrder].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      order => {
        orderRepository.create(order.customer, order.product, order.shipping, order.quantity)
        Ok(Json.obj("message" -> "Order added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    orderRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Order].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          order => {
            orderRepository.update(id, Order(id, order.customer, order.product, order.shipping, order.quantity))
            Ok(Json.obj("message" -> "Order upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Order not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    orderRepository.getByIdOption(id) map {
      case Some(c) => {
        orderRepository.delete(id)
        Ok(Json.obj("message" -> "Order deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Order not exist"))
    }

  }
}
