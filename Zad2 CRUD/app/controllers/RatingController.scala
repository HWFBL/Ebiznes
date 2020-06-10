package controllers

import javax.inject.Inject
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{MessagesRequest, _}
import repositories.{CustomerRepository, ProductRepository, RatingRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class RatingController @Inject()(ratingRepository: RatingRepository, customerRepository: CustomerRepository, productRepository: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val ratingForm: Form[RatingForm] = Form {
    mapping(
      "customer" -> longNumber,
      "value" -> number,
      "product" -> longNumber

    )(RatingForm.apply)(RatingForm.unapply)
  }

  val updateratingForm: Form[UpdateRatingForm] = Form {
    mapping(
      "id" -> longNumber,
      "customer" -> longNumber,
      "value" -> number,
      "product" -> longNumber

    )(UpdateRatingForm.apply)(UpdateRatingForm.unapply)
  }


  def add = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val products: Seq[Product] = Await.result(productRepository.list(), Duration.Inf)
    val cust: Seq[Customer] = Await.result(customerRepository.list(), Duration.Inf)

    Future.successful(Ok(views.html.rating.addrating(ratingForm, products, cust)))
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

    ratingForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.rating.addrating(errorForm, prod, cust))
        )
      },
      rat => {
        ratingRepository.create(rat.customer, rat.value, rat.product).map { _ =>
          Redirect(routes.RatingController.add()).flashing("success" -> "rating added")
        }
      }
    )

  }

  def delete(id: Long) = Action {
    ratingRepository.delete(id)
    Redirect("/ratings")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
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
    val rating = ratingRepository.getById(id)
    rating.map(rat => {
      val ratForm = updateratingForm.fill(UpdateRatingForm(rat.id, rat.customerId, rat.value, rat.product))
      //  id, product.name, product.description, product.category)
      //updateProductForm.fill(prodForm)
      Ok(views.html.rating.updaterating(ratForm, prod, cust))
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


    updateratingForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.rating.updaterating(errorForm, prod, cust))
        )
      },
      rat => {
        ratingRepository.update(rat.id, Rating(rat.id, rat.customer, rat.value, rat.product)).map { _ =>
          Redirect(routes.RatingController.add()).flashing("success" -> "rating added")
        }
      }
    )

  }

  def getAll = Action.async {
    implicit request =>
      val rating = ratingRepository.list()
      rating.map(ratings => Ok(views.html.rating.ratings(ratings)))
  }

  def get(id: Long) = Action.async { implicit request =>
    val rating = ratingRepository.getByIdOption(id)
    rating.map(ratings => ratings match {
      case Some(c) => Ok(views.html.rating.rating(c))
      case None => Redirect(routes.RatingController.getAll)
    })
  }

}

case class RatingForm(customer: Long, value: Int, product: Long)

case class UpdateRatingForm(id: Long, customer: Long, value: Int, product: Long)
