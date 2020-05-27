package controllers.api

import javax.inject.Inject
import models.Comment
import play.api.libs.json._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{CommentRepository, ProductRepository, RatingRepository}

import scala.concurrent.{ExecutionContext, Future}

case class CreateComment(content: String, product: Long, rating: Long)

object CreateComment {
  implicit val commentFormat = Json.format[CreateComment]
}

class CommentApiController @Inject()(productRepository: ProductRepository, ratingRepository: RatingRepository, commentRepository: CommentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getAll = Action.async { implicit request =>
    val comments = commentRepository.list
    comments.map (comment => Ok(Json.toJson(comment)))
  }

  def get(id: Long) = Action.async { implicit request =>
    commentRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateComment].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      comment => {
        commentRepository.create(comment.content, comment.product, comment.rating)
        Ok(Json.obj("message" -> "Comment added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    commentRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Comment].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          comment => {
            commentRepository.update(id, Comment(id, comment.content, comment.product, comment.rating))
            Ok(Json.obj("message" -> "Comment upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    commentRepository.getByIdOption(id) map {
      case Some(c) => {
        commentRepository.delete(id)
        Ok(Json.obj("message" -> "Comment deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }

  }

}
