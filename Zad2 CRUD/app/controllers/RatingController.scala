package controllers

import javax.inject.Inject
import play.api.mvc._
import repositories.{CustomerRepository, RatingRepository}

import scala.concurrent.ExecutionContext

class RatingController @Inject()(ratingRepository: RatingRepository,customerRepository: CustomerRepository , cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {


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
      val rating = ratingRepository.list()
      rating.map(ratings => Ok(views.html.ratings(ratings)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val rating = ratingRepository.getByIdOption(id)
    rating.map(ratings => ratings match {
      case Some(c) => Ok(views.html.rating(c))
      case None => Redirect(routes.RatingController.getAll)
    })
  }

}