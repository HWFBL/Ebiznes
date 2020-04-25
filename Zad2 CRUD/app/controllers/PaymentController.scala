package controllers

import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.PaymentRepository

import scala.concurrent.ExecutionContext

class PaymentController @Inject() (paymentRepository: PaymentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
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
      val pay = paymentRepository.list
      pay.map(payment => Ok(views.html.payments(payment)))
  }

  def get(id: Long) = Action.async {
    val pay = paymentRepository.getByIdOption(id)
    pay.map(payment => payment match {
      case Some(c) => Ok(views.html.payment(c))
      case None => Redirect(routes.PaymentController.getAll)
    })
  }
}
