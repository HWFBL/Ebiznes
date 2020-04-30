package controllers

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{PhotoRepository, ProductRepository}
import models.Product

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PhotoController @Inject()(photoRepository: PhotoRepository, productRepository: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val photoForm: Form[PhotoForm] = Form {
    mapping(
      "photo" -> byteNumber,
      "product" -> longNumber,

    )(PhotoForm.apply)(PhotoForm.unapply)
  }

  val updatephotoForm: Form[UpdatePhotoForm] = Form {
    mapping(
      "id" -> longNumber,
      "photo" -> byteNumber,
      "product" -> longNumber,

    )(UpdatePhotoForm.apply)(UpdatePhotoForm.unapply)
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val prod = productRepository.list
    prod.map(cat => Ok(views.html.photo.addphoto(photoForm, cat)))
  }

  def addHandle = Action.async { implicit request =>
    var prod: Seq[Product] = Seq[Product]()
    val products = productRepository.list().onComplete {
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
        photoRepository.create(photo.photo, photo.product).map { _ =>
          Redirect(routes.PhotoController.add()).flashing("success" -> "product.created")
        }
      }
    )

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


  case class PhotoForm(photo: Byte, product: Long)

  case class UpdatePhotoForm(id: Long, photo: Byte, product: Long)