package controllers

import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.OrderRepository

import scala.concurrent.ExecutionContext

class OrderController @Inject() (orderRepository: OrderRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
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
      val ord = orderRepository.list
      ord.map(orders => Ok(views.html.orders(orders)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val ord = orderRepository.getByIdOption(id)
    ord.map(order => order match {
      case Some(c) => Ok(views.html.order(c))
      case None => Redirect(routes.OrderController.getAll)
    })
  }
}
