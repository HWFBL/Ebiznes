package controllers

import java.sql.Date
import java.time.LocalDate

import javax.inject.Inject
import models.Payment
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import repositories.PaymentRepository
import play.api.data.format.Formats._

import scala.concurrent.{ExecutionContext, Future}

class PaymentController @Inject() (paymentRepository: PaymentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

  val paymentForm: Form[CreatePaymentForm] = Form {
    mapping(
      "totalPrice" -> of(doubleFormat),
      "date" -> sqlDate,
      "isDone" -> number,

    )(CreatePaymentForm.apply)(CreatePaymentForm.unapply)
  }

  val paymentUpdateForm: Form[UpdatePaymentForm] = Form {
    mapping(
      "id" -> longNumber,
      "totalPrice" -> of(doubleFormat),
      "date" -> sqlDate,
      "isDone" -> number,

    )(UpdatePaymentForm.apply)(UpdatePaymentForm.unapply)
  }

  def add = Action { implicit request =>
    Ok(views.html.payment.addPayment(paymentForm))
  }

  def addHandle = Action.async { implicit request =>
    paymentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment.addPayment(errorForm))
        )
      },
      payment => {
        paymentRepository.create(payment.totalPrice, payment.date, payment.isDone).map { _ =>
          Redirect(routes.PaymentController.add()).flashing("succes" -> "payment created")
        }
      }
    )
  }

  def delete(id: Long) = Action {
    paymentRepository.delete(id)
    Redirect("/payments")
  }

  def update(id: Long) = Action.async {
    implicit request =>

      val payment = paymentRepository.getById(id)
      payment.map(pay => {
        val payForm = paymentUpdateForm.fill(UpdatePaymentForm(pay.id, pay.totalPrice, pay.date, pay.isDone))
        Ok(views.html.payment.udpatePayment(payForm))
      })
  }

  def updateHandle = Action.async { implicit request =>

    paymentUpdateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.payment.udpatePayment(errorForm))
        )
      },
      payment => {
        paymentRepository.update(payment.id, Payment(payment.id, payment.totalPrice, payment.date, payment.isDone
        ) ).map { _ =>
          Redirect(routes.PaymentController.update(payment.id)).flashing("succes" -> "payment created")
        }
      }
    )

  }

  def getAll = Action.async {
    implicit request =>
      val pay = paymentRepository.list
      pay.map(payment => Ok(views.html.payment.payments(payment)))
  }

  def get(id: Long) = Action.async {
    val pay = paymentRepository.getByIdOption(id)
    pay.map(payment => payment match {
      case Some(c) => Ok(views.html.payment.payment(c))
      case None => Redirect(routes.PaymentController.getAll)
    })
  }
}

case class CreatePaymentForm(totalPrice: Double, date: Date, isDone: Int)

case class UpdatePaymentForm(id: Long, totalPrice: Double, date: Date, isDone: Int)