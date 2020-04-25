package controllers

import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.ShippingRepository

import scala.concurrent.ExecutionContext

class ShippingController @Inject()(shippingRepository: ShippingRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def add = Action.async {
    Ok("")
  }

  def delete(ratingId: Long) = Action {
    Ok("")
  }

  def update(ratingId: Long) = Action.async {
    Ok("")
  }

  def getAll = Action.async {
    implicit request =>
      val ship = shippingRepository.list()
      ship.map(shipping => Ok(views.html.shippings))
  }

  def get(id: Long) = Action.async { implicit request =>
    val ship = shippingRepository.getByIdOption(id)
    ship.map(shippings => shippings match {
      case Some(c) => Ok(views.html.shipping(c))
      case None => Redirect(routes.ShippingController.getAll)
    })
  }
}
