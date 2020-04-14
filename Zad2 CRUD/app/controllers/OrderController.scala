package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class OrderController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def get(orderId: String) = Action {
    Ok("")
  }

  def getAll = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(orderId: String) = Action {
    Ok("")
  }

  def update(orderId: String) = Action {
    Ok("")
  }

}
