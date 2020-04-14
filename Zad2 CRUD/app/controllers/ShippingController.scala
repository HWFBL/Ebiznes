package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class ShippingController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def get(shippingId: String) = Action {
    Ok("")
  }

  def getAll = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(shippingId: String) = Action {
    Ok("")
  }

  def update(shippingId: String) = Action {
    Ok("")
  }

}
