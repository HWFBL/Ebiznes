package controllers

import javax.inject.Inject
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.PhotoRepository

import scala.concurrent.ExecutionContext

class PhotoController @Inject() (photoRepository: PhotoRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def add = Action {
    Ok("")
  }

  def delete(photoId: Long) = Action {
    Ok("")
  }

  def update(photoId: Long) = Action {
    Ok("")
  }

  def getAll = Action.async {
    implicit request =>
      val foto = photoRepository.list
      foto.map(photo => Ok(views.html.photos(photo)))
  }

  def get(id: Long) = Action.async {implicit request =>
    val foto = photoRepository.getByIdOption(id)
    foto.map(ratings => ratings match {
      case Some(c) => Ok(views.html.photo(c))
      case None => Redirect(routes.PhotoController.getAll)
    })
  }
}
