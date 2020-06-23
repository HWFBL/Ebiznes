package controllers.api

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import models.{Category, Comment, UserRoles}
import play.api.libs.json._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{CategoryRepository, ProductRepository, RatingRepository}
import utils.auth.{JwtEnv, RoleJWTAuthorization}

import scala.concurrent.{ExecutionContext, Future}

case class CreateCategory(name: String)

object CreateCategory {
  implicit val commentFormat = Json.format[CreateCategory]
}

class CategoryApiController @Inject()(productRepository: ProductRepository, ratingRepository: RatingRepository, categoryRepository: CategoryRepository, silhouette: Silhouette[JwtEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def getAll = Action.async { implicit request =>
    val comments = categoryRepository.list
    comments.map (comment => Ok(Json.toJson(comment)))
  }

  def get(id: Int) = Action.async { implicit request =>
    categoryRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Customer)) { implicit request =>
    val body = request.body.asJson.get
    body.validate[CreateCategory].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      comment => {
        categoryRepository.create(comment.name)
        Ok(Json.obj("message" -> "Comment added"))
      }
    )
  }

  def update(id: Int) = silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Customer)).async(parse.json) {implicit request =>
    categoryRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Category].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          comment => {
            categoryRepository.update(id, Category(id, comment.name))
            Ok(Json.obj("message" -> "Comment upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }
  }

  def delete(id: Int) =  silhouette.SecuredAction(RoleJWTAuthorization(UserRoles.Admin)).async  {  implicit request =>
    categoryRepository.getByIdOption(id) map {
      case Some(c) => {
        categoryRepository.delete(id)
        Ok(Json.obj("message" -> "Comment deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }

  }

}
