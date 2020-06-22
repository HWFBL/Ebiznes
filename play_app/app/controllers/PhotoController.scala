package controllers

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{PhotoRepository, ProductRepository}
import models._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PhotoController @Inject()(photoRepository: PhotoRepository, productRepository: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val photoForm: Form[PhotoForm] = Form {
    mapping(
      "photo" -> nonEmptyText,
      "link" -> nonEmptyText,

    )(PhotoForm.apply)(PhotoForm.unapply)
  }

  val updatephotoForm: Form[UpdatePhotoForm] = Form {
    mapping(
      "id" -> longNumber,
      "photo" -> nonEmptyText,
      "link" -> nonEmptyText,

    )(UpdatePhotoForm.apply)(UpdatePhotoForm.unapply)
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val prod = productRepository.list
    prod.map(cat => Ok(views.html.photo.addphoto(photoForm, cat)))
  }

  def addHandle = Action.async { implicit request =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")
    }

    photoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.photo.addphoto(errorForm, prod))
        )
      },
      photo => {
        photoRepository.create(photo.photo, photo.link).map { _ =>
          Redirect(routes.PhotoController.add()).flashing("success" -> "product.created")
        }
      }
    )

  }

  def delete(id: Long) = Action {
    photoRepository.delete(id)
    Redirect("/photos")
    Redirect("/photos")
  }

  def update(photoId: Long) = Action.async { implicit request =>

    var prod: Seq[Photo] = Seq[Photo]()
    photoRepository.list.onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")
    }

    val photo = photoRepository.getById(photoId)
    photo.map(ph => {
      val phForm = updatephotoForm.fill(UpdatePhotoForm(ph.id, ph.photo, ph.link))
      Ok(views.html.photo.updatephoto(phForm, prod))
    })
  }

  def updateHandle = Action.async { implicit request =>

    var prod: Seq[Photo] = Seq[Photo]()
    photoRepository.list.onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")
    }

    updatephotoForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.photo.updatephoto(errorForm, prod))
        )
      },
      photo => {
        photoRepository.update(photo.id, Photo(photo.id, photo.photo, photo.link)).map { _ =>
          Redirect(routes.PhotoController.update(photo.id)).flashing("success" -> "photo updated")
        }
      }
    )
  }

  def getAll = Action.async {
    implicit request =>
      val foto = photoRepository.list
      foto.map(photo => Ok(views.html.photo.photos(photo)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val foto = photoRepository.getByIdOption(id)
    foto.map(ratings => ratings match {
      case Some(c) => Ok(views.html.photo.photo(c))
      case None => Redirect(routes.PhotoController.getAll)
    })
  }


}


case class PhotoForm(photo: String, link: String)

case class UpdatePhotoForm(id: Long, photo: String, link: String)