package controllers

import javax.inject.{Inject, Singleton}
import models.Customer
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repositories.CustomerRepository
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext

@Singleton
class CustomerController @Inject()(customerRepo: CustomerRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val createCustomerForm: Form[CreateCustomerForm] = Form {
    mapping(
      "forename" -> nonEmptyText,
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
    )(CreateCustomerForm.apply)(CreateCustomerForm.unapply)
  }

  val updateCustomerForm: Form[UpdateCustomerForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> number,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def getCustomers = Action.async {
    implicit request =>
      val customers = customerRepo.list
      customers.map(customer => Ok(views.html.customers(customer)))
  }

  def getCustomer(id: Long) = Action.async {
    val customer = customerRepo.getByIdOption(id)
    customer.map(customer => customer match {
      case Some(c) => Ok(views.html.customer(c))
      case None => Redirect(routes.)
    })
  }

  def delete(id: Long) = Action {
    customerRepo.delete(id)
    Redirect("/customers")
  }

  def update(id: Long) = Action.async {
    Ok("")
  }


  def add() = Action.async {
    Ok("")
  }
}


case class CreateCustomerForm(forename: String, name: String, email: String)
case class UpdateCustomerForm(id: Long, forename: String, name: String, email: String)