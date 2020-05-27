package controllers.api

import javax.inject.Inject
import models.Rating
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.RatingRepository

import scala.concurrent.ExecutionContext

case class CreateRating(customer_id: Long, value: Int, product: Long)

object CreateRating {
  implicit val formatRating = Json.format[CreateRating]
}

class RatingApiController @Inject()(ratingRepository: RatingRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def getAll = Action.async { implicit request =>
    val rating = ratingRepository.list()
    rating.map (ratings => Ok(Json.toJson(ratings)))
  }

  def get(id: Long) = Action.async { implicit request =>
    ratingRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateRating].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      rating => {
        ratingRepository.create(rating.customer_id, rating.value, rating.product)
        Ok(Json.obj("message" -> "Rating added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    ratingRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Rating].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          rating => {
            ratingRepository.update(id, Rating(id, rating.customer_id, rating.value, rating.product))
            Ok(Json.obj("message" -> "Rating upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Rating not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    ratingRepository.getByIdOption(id) map {
      case Some(c) => {
        ratingRepository.delete(id)
        Ok(Json.obj("message" -> "Rating deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Rating not exist"))
    }

  }
}
