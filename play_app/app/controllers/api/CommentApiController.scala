package controllers.api

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import models.{Comment, UserRoles}
import play.api.libs.json._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{CommentRepository, ProductRepository, RatingRepository}
import utils.auth.{JwtEnv, RoleJWTAuthorization}

import scala.concurrent.{ExecutionContext, Future}

case class CreateComment(content: String, product: Long, rating: Long)

object CreateComment {
  implicit val commentFormat = Json.format[CreateComment]
}

class CommentApiController @Inject()(productRepository: ProductRepository, ratingRepository: RatingRepository, commentRepository: CommentRepository, silhouette: Silhouette[JwtEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getAll = Action.async { implicit request =>
    val comments = commentRepository.list
    comments.map (comment => Ok(Json.toJson(comment)))
  }

  def get(id: Long) = Action.async { implicit request =>
    commentRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Customer)) { implicit request =>
    val body = request.body.asJson.get
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

  def update(id: Long) = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Customer)).async(parse.json) {implicit request =>
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

  def delete(id: Long) =  silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async  {  implicit request =>
    commentRepository.getByIdOption(id) map {
      case Some(c) => {
        commentRepository.delete(id)
        Ok(Json.obj("message" -> "Comment deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }

  }

}
