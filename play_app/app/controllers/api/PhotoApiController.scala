package controllers.api

import javax.inject.Inject
import models.Photo
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.PhotoRepository

import scala.concurrent.ExecutionContext

case class CreatePhoto(photo: String, link: String)

object CreatePhoto {
  implicit val formatPhoto = Json.format[CreatePhoto]
}

class PhotoApiController @Inject()(photoRepository: PhotoRepository,  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def getAll = Action.async { implicit request =>
    val photo = photoRepository.list
    photo.map (photos => Ok(Json.toJson(photos)))
  }

  def get(id: Long) = Action.async { implicit request =>
    photoRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreatePhoto].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      photo => {
        photoRepository.create(photo.photo, photo.link)
        Ok(Json.obj("message" -> "Photo added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    photoRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Photo].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          photo => {
            photoRepository.update(id, Photo(id, photo.photo, photo.link))
            Ok(Json.obj("message" -> "Photo upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Photo not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    photoRepository.getByIdOption(id) map {
      case Some(c) => {
        photoRepository.delete(id)
        Ok(Json.obj("message" -> "Photo deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Photo not exist"))
    }

  }
}
