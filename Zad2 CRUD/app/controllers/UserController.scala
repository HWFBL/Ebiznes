package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def get(userId: String) = Action {
    Ok("")
  }

  def getAll = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(userId: String) = Action {
    Ok("")
  }

  def update(userId: String) = Action {
    Ok("")
  }

}
