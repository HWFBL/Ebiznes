package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

class RatingController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def add = Action {
    Ok("")
  }

  def delete(ratingId: String) = Action {
    Ok("")
  }

  def update(ratingId: String) = Action {
    Ok("")
  }


}
