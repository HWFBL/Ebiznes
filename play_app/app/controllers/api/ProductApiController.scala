package controllers.api

import javax.inject.Inject
import models.Product
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.ProductRepository
import com.mohiva.play.silhouette.api.Silhouette
import utils.auth.JwtEnv

import scala.concurrent.ExecutionContext

case class CreateProduct(name: String, description: String, photo: Long, category: Int, price: Double, quantity: Int)

object CreateProduct {
  implicit val formatProduct = Json.format[CreateProduct]
}

class ProductApiController @Inject()(productRepository: ProductRepository, silhouette: Silhouette[JwtEnv], cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def getAll = silhouette.UserAwareAction.async { implicit request =>
    val product = productRepository.list()
    product.map(products => Ok(Json.toJson(products)))
  }

  def get(id: Long) = Action.async { implicit request =>
    productRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }

  def getByCategory(id: Int) = Action.async { implicit request =>
    val products = productRepository.getByCategory(id)
  products.map(p => Ok(Json.toJson(p)))

  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateProduct].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      product => {
        productRepository.create(product.name, product.description, product.photo, product.category, product.price, product.quantity)
        Ok(Json.obj("message" -> "Product added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    productRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Product].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          product => {
            productRepository.update(id, Product(id, product.name, product.description, product.photo, product.category, product.price, product.quantity))
            Ok(Json.obj("message" -> "Product upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Product not exist"))
    }
  }

  def delete(id: Long) = Action.async {  implicit request =>
    productRepository.getByIdOption(id) map {
      case Some(c) => {
        productRepository.delete(id)
        Ok(Json.obj("message" -> "Product deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Product not exist"))
    }

  }
}
