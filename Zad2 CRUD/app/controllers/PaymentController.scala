package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class PaymentController @Inject()(cc: ControllerComponents) extends AbstractController(cc)  {

  def getAll = Action {
    Ok("")
  }

  def update(payId: String) = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(payId: String) = Action {
    Ok("")
  }

}
