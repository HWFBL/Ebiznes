package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class CartController @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  def getAll = Action {
    Ok("")
  }

  def get(cartId: String) = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(cartId: String) = Action {
    Ok("")
  }

}
