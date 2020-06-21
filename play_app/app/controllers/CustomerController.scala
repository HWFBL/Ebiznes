//package controllers
//
//import javax.inject.{Inject, Singleton}
//import models.Customer
//import play.api.data.Form
//import play.api.data.Forms._
//import play.api.mvc._
//import repositories.CustomerRepository
////import com.mohiva.play.silhouette.api.LoginInfo
////import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
////import com.mohiva.play.silhouette.api.services.IdentityService
////import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
////import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
//
//import scala.util.{Failure, Success}
//import scala.concurrent.{ExecutionContext, Future}
//
//@Singleton
//class CustomerController @Inject()(customerRepo: CustomerRepository,  cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
//
//  val createCustomerForm: Form[CreateCustomerForm] = Form {
//    mapping(
//      "forename" -> nonEmptyText,
//      "name" -> nonEmptyText,
//      "email" -> nonEmptyText,
//    )(CreateCustomerForm.apply)(CreateCustomerForm.unapply)
//  }
//
//  val updateCustomerForm: Form[UpdateCustomerForm] = Form {
//    mapping(
//      "id" -> longNumber,
//      "forename" -> nonEmptyText,
//      "name" -> nonEmptyText,
//      "email" -> nonEmptyText,
//    )(UpdateCustomerForm.apply)(UpdateCustomerForm.unapply)
//  }
//
//  def getCustomers = Action.async {
//    implicit request =>
//      val customers = customerRepo.list
//      customers.map(customer => Ok(views.html.customer.customers(customer)))
//  }
//
//  def getCustomer(id: Long) = Action.async {
//    val customer = customerRepo.getByIdOption(id)
//    customer.map(customer => customer match {
//      case Some(c) => Ok(views.html.customer.customer(c))
//      case None => Redirect(routes.CustomerController.getCustomers())
//    })
//  }
//
//  def delete(id: Long) = Action {
//    customerRepo.delete(id)
//    Redirect("/customers")
//  }
//
//  def update(id: Long) = Action.async {
//    implicit request =>
//
//      val customer = customerRepo.getById(id)
//      customer.map(cust => {
//        val custForm = updateCustomerForm.fill(UpdateCustomerForm(cust.id, cust.forename, cust.name, cust.email ))
//        Ok(views.html.customer.updatecustomer(custForm))
//      })
//  }
//
//  def updateHandle = Action.async { implicit request =>
//
//    updateCustomerForm.bindFromRequest.fold(
//      errorForm => {
//        Future.successful(
//          BadRequest(views.html.customer.updatecustomer(errorForm))
//        )
//      },
//      cust => {
//        customerRepo.update(cust.id, Customer(cust.id, cust.forename, cust.name, cust.email)).map { _ =>
//          Redirect(routes.CustomerController.update(cust.id)).flashing("succes" -> "customer created")
//        }
//      }
//    )
//
//  }
//
//
//  def add() = Action { implicit request =>
//    Ok(views.html.customer.addcustomer(createCustomerForm))
//  }
//
//  def addHandle = Action.async { implicit request =>
//    createCustomerForm.bindFromRequest.fold(
//      errorForm => {
//        Future.successful(
//          BadRequest(views.html.customer.addcustomer(errorForm))
//        )
//      },
//      cust => {
//        customerRepo.create(cust.forename, cust.name, cust.email).map { _ =>
//          Redirect(routes.CustomerController.add()).flashing("succes" -> "customer created")
//        }
//      }
//    )
//  }
//}
//
//
//case class CreateCustomerForm(forename: String, name: String, email: String)
//case class UpdateCustomerForm(id: Long, forename: String, name: String, email: String)