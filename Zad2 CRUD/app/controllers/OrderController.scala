package controllers

import javax.inject.Inject
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import repositories.{CustomerRepository, OrderRepository, ProductRepository, ShippingRepository}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class OrderController @Inject()(orderRepository: OrderRepository, productRepository: ProductRepository, customerRepository: CustomerRepository, shippingRepository: ShippingRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val orderForm: Form[OrderForm] = Form {
    mapping(
      "customer" -> longNumber,
      "product" -> longNumber,
      "shipping" -> longNumber,
      "quantity" -> number,

    )(OrderForm.apply)(OrderForm.unapply)
  }

  val updateorderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "customer" -> longNumber,
      "product" -> longNumber,
      "shipping" -> longNumber,
      "quantity" -> number,

    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def add = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val products: Seq[Product] = Await.result(productRepository.list(), Duration.Inf)
    val cust: Seq[Customer] = Await.result(customerRepository.list(), Duration.Inf)
    val ship: Seq[Shipping] = Await.result(shippingRepository.list(), Duration.Inf)

    Future.successful(Ok(views.html.order.addorder(orderForm, products, cust, ship)))
  }

  def addHandle = Action.async { implicit request =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")

    }

    var cust: Seq[Customer] = Seq[Customer]()
    customerRepository.list().onComplete {
      case Success(c) => cust = c
      case Failure(_) => print("fail")
    }

    var ship: Seq[Shipping] = Seq[Shipping]()
    val shipping = shippingRepository.list().onComplete {
      case Success(c) => ship = c
      case Failure(_) => print("fail")
    }

    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order.addorder(errorForm, prod, cust, ship))
        )
      },
      ord => {
        orderRepository.create(ord.customer, ord.product, ord.shipping, ord.quantity).map { _ =>
          Redirect(routes.OrderController.add()).flashing("success" -> "order added")
        }
      }
    )

  }

  def delete(ratingId: Long) = Action {
    orderRepository.delete(ratingId)
    Redirect("/orders")
  }

  def update(id: Long) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")

    }

    var cust: Seq[Customer] = Seq[Customer]()
    customerRepository.list().onComplete {
      case Success(c) => cust = c
      case Failure(_) => print("fail")
    }

    var ship: Seq[Shipping] = Seq[Shipping]()
    shippingRepository.list().onComplete {
      case Success(c) => ship = c
      case Failure(_) => print("fail")
    }

    val order = orderRepository.getById(id)
    order.map(o => {
      val oForm = updateorderForm.fill(UpdateOrderForm(o.id, o.customer, o.product, o.shipping, o.quantity))
      //  id, product.name, product.description, product.category)
      //updateProductForm.fill(prodForm)
      Ok(views.html.order.updateorder(oForm, prod, cust, ship))
    })
  }


  def updateHandle = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")

    }

    var cust: Seq[Customer] = Seq[Customer]()
    customerRepository.list().onComplete {
      case Success(c) => cust = c
      case Failure(_) => print("fail")
    }

    var ship: Seq[Shipping] = Seq[Shipping]()
    val shipping = shippingRepository.list().onComplete {
      case Success(c) => ship = c
      case Failure(_) => print("fail")
    }

    updateorderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order.updateorder(errorForm, prod, cust, ship))
        )
      },
      o => {
        orderRepository.update(o.id, Order(o.id, o.customer, o.product, o.shipping, o.quantity)).map { _ =>
          Redirect(routes.OrderController.add()).flashing("success" -> "order updated")
        }
      }
    )
  }

  def getAll = Action.async {
    implicit request =>
      val ord = orderRepository.list
      ord.map(orders => Ok(views.html.order.orders(orders)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val ord = orderRepository.getByIdOption(id)
    ord.map(order => order match {
      case Some(c) => Ok(views.html.order.order(c))
      case None => Redirect(routes.OrderController.getAll)
    })
  }

}

case class OrderForm(customer: Long, product: Long, shipping: Long, quantity: Int)

case class UpdateOrderForm(id: Long, customer: Long, product: Long, shipping: Long, quantity: Int)