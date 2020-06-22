package controllers

import javax.inject.Inject
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{MessagesRequest, _}
import repositories.{CommentRepository, ProductRepository, RatingRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class CommentController @Inject()(productRepository: ProductRepository, ratingRepository: RatingRepository, commentRepository: CommentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val commentForm: Form[CommentForm] = Form {
    mapping(
      "content" -> nonEmptyText,
      "product" -> longNumber,


    )(CommentForm.apply)(CommentForm.unapply)
  }

  val updatecommentForm: Form[UpdateCommentForm] = Form {
    mapping(
      "id" -> longNumber,
      "content" -> nonEmptyText,
      "product" -> longNumber,


    )(UpdateCommentForm.apply)(UpdateCommentForm.unapply)
  }

  def add = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val products: Seq[Product] = Await.result(productRepository.list(), Duration.Inf)
    val rating: Seq[Rating] = Await.result(ratingRepository.list(), Duration.Inf)

    Future.successful(Ok(views.html.comment.addcomment(commentForm, products, rating)))
  }

  def addHandle = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")

    }

    var rat: Seq[Rating] = Seq[Rating]()
    ratingRepository.list().onComplete {
      case Success(c) => rat = c
      case Failure(_) => print("fail")
    }

    commentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.comment.addcomment(errorForm, prod, rat))
        )
      },
      com => {
        commentRepository.create(com.content, com.product).map { _ =>
          Redirect(routes.CommentController.add()).flashing("success" -> "comment added")
        }
      }
    )

  }


  def update(id: Long) = Action.async { implicit request =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")

    }

    var rat: Seq[Rating] = Seq[Rating]()
    ratingRepository.list().onComplete {
      case Success(c) => rat = c
      case Failure(_) => print("fail")
    }

    val comment = commentRepository.getById(id)
    comment.map(com => {
      val comForm = updatecommentForm.fill(UpdateCommentForm(com.id, com.content, com.product))

      Ok(views.html.comment.updatecomment(comForm, prod, rat))
    })
  }


  def updateHandle = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod: Seq[Product] = Seq[Product]()
    productRepository.list().onComplete {
      case Success(cat) => prod = cat
      case Failure(_) => print("fail")

    }

    var rat: Seq[Rating] = Seq[Rating]()
    ratingRepository.list().onComplete {
      case Success(c) => rat = c
      case Failure(_) => print("fail")
    }

    updatecommentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.comment.updatecomment(errorForm, prod, rat))
        )
      },
      com => {
        commentRepository.update(com.id, Comment(com.id, com.content, com.product)).map { _ =>
          Redirect(routes.CommentController.update(com.id)).flashing("success" -> "comment updated")
        }
      }
    )
  }

  def delete(commentId: Long) = Action {
    commentRepository.delete(commentId)
    Redirect("/comments")
  }

  def getAll = Action.async {
    implicit request =>
      val comments = commentRepository.list
      comments.map(comment => Ok(views.html.comment.comments(comment)))
  }

  def get(id: Long) = Action.async {
    val comment = commentRepository.getByIdOption(id)
    comment.map(comments => comments match {
      case Some(c) => Ok(views.html.comment.comment(c))
      case None => Redirect(routes.CommentController.getAll)
    })
  }
}

case class CommentForm(content: String, product: Long)

case class UpdateCommentForm(id: Long, content: String, product: Long)