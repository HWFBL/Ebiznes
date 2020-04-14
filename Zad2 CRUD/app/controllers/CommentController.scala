package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class CommentController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("")
  }

  def update(commentId: String) = Action {
    Ok("")
  }

  def delete(commentId: String) = Action {
    Ok("")
  }

}
