package controllers

import javax.inject._
import models.{Category, Photo, Product}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.format.Formats._
import repositories.{CategoryRepository, PhotoRepository, ProductRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(productsRepo: ProductRepository, categoryRepo: CategoryRepository, photoRepository: PhotoRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "photo" -> longNumber,
      "category" -> number,
      "price" -> of(doubleFormat),
      "quantity" -> number,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "photo" -> longNumber,
      "category" -> number,
      "price" -> of(doubleFormat),
      "quantity" -> number,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val produkty = productsRepo.list()
    produkty.map(prods => Ok(views.html.product.products(prods)))
  }

  def getProduct(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val produkt = productsRepo.getByIdOption(id)
    produkt.map(product => product match {
      case Some(p) => Ok(views.html.product.product(p))
      case None => Redirect(routes.HomeController.getProducts())
    })
  }

  def delete(id: Long): Action[AnyContent] = Action {
    productsRepo.delete(id)
    Redirect("/products")
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var categ: Seq[Category] = Seq[Category]()
    categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    var phot: Seq[Photo] = Seq[Photo]()
    photoRepository.list.onComplete {
      case Success(p) => phot = p
      case Failure(_) => print("fail")
    }
    val produkt = productsRepo.getById(id)
    produkt.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.id, product.name, product.description, product.photo, product.category, product.price, product.quantity))
      //  id, product.name, product.description, product.category)
      //updateProductForm.fill(prodForm)
      Ok(views.html.product.productupdate(prodForm, categ, phot))
    })
  }

  def updateProductHandle = Action.async { implicit request =>
    var categ: Seq[Category] = Seq[Category]()
    categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    var phot: Seq[Photo] = Seq[Photo]()
    photoRepository.list.onComplete {
      case Success(ph) => phot = ph
      case Failure(_) => print("fail")
    }

    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product.productupdate(errorForm, categ, phot))
        )
      },
      product => {
        productsRepo.update(product.id, Product(product.id, product.name, product.description, product.photo, product.category, product.price, product.quantity
        )).map { _ =>
          Redirect(routes.HomeController.updateProduct(product.id)).flashing("success" -> "product updated")
        }
      }
    )

  }


  def addProduct: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = Await.result(categoryRepo.list(), Duration.Inf)
    val photos = Await.result(photoRepository.list, Duration.Inf)
    Future.successful(Ok(views.html.product.productadd(productForm, categories, photos)))
  }

  def addProductHandle = Action.async { implicit request =>
    var categ: Seq[Category] = Seq[Category]()
    categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    var phot: Seq[Photo] = Seq[Photo]()
    photoRepository.list.onComplete {
      case Success(ph) => phot = ph
      case Failure(_) => print("fail")
    }

    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product.productadd(errorForm, categ, phot))
        )
      },
      product => {
        productsRepo.create(product.name, product.description, product.photo, product.category, product.price, product.quantity).map { _ =>
          Redirect(routes.HomeController.addProduct()).flashing("success" -> "product.created")
        }
      }
    )

  }

}

case class CreateProductForm(name: String, description: String, photo: Long, category: Int, price: Double, quantity: Int)

case class UpdateProductForm(id: Long, name: String, description: String, photo: Long, category: Int, price: Double, quantity: Int)
