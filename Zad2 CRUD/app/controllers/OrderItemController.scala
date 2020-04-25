package controllers

import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.OrderItemRepository

import scala.concurrent.ExecutionContext

class OrderItemController @Inject()(orderItemRepository: OrderItemRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def add = Action {
    Ok("")
  }

  def delete(ratingId: Long) = Action {
    Ok("")
  }

  def update(ratingId: Long) = Action {
    Ok("")
  }

  def getAll = Action.async {
    implicit request =>
      val ord_it = orderItemRepository.list
      ord_it.map(orders => Ok(views.html.orderItems(orders)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val ord_it = orderItemRepository.getByIdOption(id)
    ord_it.map(order => order match {
      case Some(c) => Ok(views.html.orderItem(c))
      case None => Redirect(routes.OrderItemController.getAll)
    })
  }
}
