package controllers

import javax.inject.Inject
import models.Shipping
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.ShippingRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ShippingController @Inject()(shippingRepository: ShippingRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val shippingForm: Form[CreateShippingForm] = Form {
    mapping(
      "street" -> nonEmptyText,
      "houseNumber" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipCode" -> nonEmptyText,
    )(CreateShippingForm.apply)(CreateShippingForm.unapply)
  }

  val shippingUpdateForm: Form[UpdateShippingForm] = Form {
    mapping(
      "id" -> longNumber,
      "street" -> nonEmptyText,
      "houseNumber" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipCode" -> nonEmptyText,
    )(UpdateShippingForm.apply)(UpdateShippingForm.unapply)
  }


  def add = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.shipping.addshipping(shippingForm))
  }

  def addShippingHandler = Action.async { implicit request =>

    shippingForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.shipping.addshipping(errorForm))
        )
      },
      shipment => {
        shippingRepository.create(shipment.street, shipment.houseNumber, shipment.city, shipment.zipCode).map { _ =>
          Redirect(routes.ShippingController.add()).flashing("succes" -> "shipment.created")
        }
      }
    )
  }

  def delete(id: Long) = Action {
    shippingRepository.delete(id)
    Redirect("/shippings")
  }

  def update(id: Long) = Action.async {
    implicit request =>

      val shipment = shippingRepository.getById(id)
      shipment.map(ship => {
        val shipForm = shippingUpdateForm.fill(UpdateShippingForm(ship.id, ship.street, ship.houseNumber, ship.city, ship.zipCode))
        Ok(views.html.shipping.updateshipping(shipForm))
      })
  }

  def updateShippingHandle = Action.async { implicit request =>

    shippingUpdateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.shipping.updateshipping(errorForm))
        )
      },
      shipment => {
        shippingRepository.update(shipment.id, Shipping(shipment.id, shipment.street, shipment.houseNumber, shipment.city, shipment.zipCode
        ) ).map { _ =>
          Redirect(routes.ShippingController.update(shipment.id)).flashing("succes" -> "shipment created")
        }
      }
    )

  }

  def getAll = Action.async {
    implicit request =>
      val ship = shippingRepository.list()
      ship.map(shipping => Ok(views.html.shipping.shippings(shipping)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val ship = shippingRepository.getByIdOption(id)
    ship.map(shippings => shippings match {
      case Some(c) => Ok(views.html.shipping.shipping(c))
      case None => Redirect(routes.ShippingController.getAll)
    })
  }
}

case class CreateShippingForm(street: String, houseNumber: String, city: String, zipCode: String)

case class UpdateShippingForm(id: Long, street: String, houseNumber: String, city: String, zipCode: String)