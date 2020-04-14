package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class CategoryController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def get(categoryId: String) = Action {
    Ok("")
  }

  def getAll = Action {
    Ok("")
  }

  def add = Action {
    Ok("")
  }

  def delete(categoryId: String) = Action {
    Ok("")
  }

  def update(categoryId: String) = Action {
    Ok("")
  }

}
