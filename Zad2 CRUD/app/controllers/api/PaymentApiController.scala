package controllers.api

import java.sql.Date

import javax.inject.Inject
import models.Payment
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.PaymentRepository

import scala.concurrent.ExecutionContext

case class CreatePayment(total_price: Double, date: Date,is_done: Int)

object CreatePayment {
  implicit val formatPayment = Json.format[CreatePayment]
}

class PaymentApiController @Inject() (paymentRepository: PaymentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  def getAll = Action.async { implicit request =>
    val payment = paymentRepository.list()
    payment.map (payments => Ok(Json.toJson(payments)))
  }

  def get(id: Long) = Action.async { implicit request =>
    paymentRepository.getByIdOption(id) map {
      case Some(c) => Ok(Json.toJson(c))
    }
  }


  def add = Action(parse.json) { implicit request =>
    val body = request.body
    body.validate[CreatePayment].fold(
      error => {
        BadRequest(Json.obj("message" -> JsError.toJson(error)))
      },
      payment => {
        paymentRepository.create(payment.total_price, payment.date, payment.is_done)
        Ok(Json.obj("message" -> "Payment added"))
      }
    )
  }

  def update(id: Long) = Action.async(parse.json) {implicit request =>
    paymentRepository.getByIdOption(id) map {
      case Some(c) => {
        val body = request.body
        body.validate[Payment].fold(
          error => {
            BadRequest(Json.obj("message" -> JsError.toJson(error)))
          },
          payment => {
            paymentRepository.update(id, Payment(id, payment.total_price, payment.date, payment.is_done))
            Ok(Json.obj("message" -> "Payment upated"))
          }
        )
      }
      case None => NotFound(Json.obj("message" -> "Comment not exist"))
    }
  }

  def  delete(id: Long) = Action.async {  implicit request =>
    paymentRepository.getByIdOption(id) map {
      case Some(c) => {
        paymentRepository.delete(id)
        Ok(Json.obj("message" -> "Payment deleted"))
      }
      case None => NotFound(Json.obj("message" -> "Payment not exist"))
    }

  }
}
