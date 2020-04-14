package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class OpinionController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def get(opinionId: String) = Action {
    Ok("")
  }

  def getAll = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(opinionId: String) = Action {
    Ok("")
  }

  def update(opinionId: String) = Action {
    Ok("")
  }


}
