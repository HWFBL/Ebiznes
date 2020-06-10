package controllers.api

import javax.inject.Inject
import models.Shipping
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.ShippingRepository

import scala.concurrent.ExecutionContext

case class CreateShipping( street: String, houseNumber: String, city: String, zipCode: String)

object CreateShipping {
  implicit val formatShipping = Json.format[CreateShipping]
}

class ShippingApiController  @Inject()(shippingRepository: ShippingRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){
  def getAll = Action.async { implicit request =>
    val shipping = shippingRepository.list()
    shipping.map (shippings => Ok(Json.toJson(shippings)))
  }

  def get(id: Long) = Action.async { implicit request =>
    shippingRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateShipping].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      shipping => {
        shippingRepository.create(shipping.street, shipping.houseNumber, shipping.city, shipping.zipCode)
        Ok(Json.obj("message" -> "Shipping added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    shippingRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Shipping].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          shipping => {
            shippingRepository.update(id, Shipping(id, shipping.street, shipping.houseNumber, shipping.city, shipping.zipCode))
            Ok(Json.obj("message" -> "Shipping upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Shipping not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    shippingRepository.getByIdOption(id) map {
      case Some(c) => {
        shippingRepository.delete(id)
        Ok(Json.obj("message" -> "Shipping deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Shipping not exist"))
    }

  }
}
