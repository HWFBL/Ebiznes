package controllers.api

import javax.inject.{Inject, Singleton}
import models.Customer
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.CustomerRepository

import scala.concurrent.ExecutionContext

case class CreateCustomer(forename: String, name: String, email: String)

object CreateCustomer {
  implicit val customerFormat = Json.format[CreateCustomer]
}

@Singleton
class CustomerApiController @Inject()(customerRepo: CustomerRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def getAll = Action.async { implicit request =>
    val customer = customerRepo.list()
    customer.map (customers => Ok(Json.toJson(customers)))
  }

  def get(id: Long) = Action.async { implicit request =>
    customerRepo.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreateCustomer].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      customer => {
        customerRepo.create(customer.forename, customer.name, customer.email)
        Ok(Json.obj("message" -> "Customer added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    customerRepo.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Customer].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          customer => {
            customerRepo.update(id, Customer(id, customer.forename, customer.name, customer.email))
            Ok(Json.obj("message" -> "Customer upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    customerRepo.getByIdOption(id) map {
      case Some(c) => {
        customerRepo.delete(id)
        Ok(Json.obj("message" -> "Customer deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Customer not exist"))
    }

  }
}
