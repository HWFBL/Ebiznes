package controllers

import javax.inject.Inject
import play.api.mvc._
import repositories.{ CommentRepository, ProductRepository, RatingRepository}

import scala.concurrent.{ExecutionContext, Future}

class CommentController @Inject()(productsRepo: ProductRepository, ratingRepository: RatingRepository, commentRepository: CommentRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc)  {

  def create = Action {
    Ok("")
  }

  def update(commentId: Long) = Action {
    Ok("")
  }

  def delete(commentId: Long) = Action {
    Ok("")
  }

  def getAll = Action.async {
    implicit request =>
      val comments = commentRepository.list
      comments.map(comment => Ok(views.html.comments(comment)))
  }

  def get(id: Long) = Action.async {
    val comment = commentRepository.getByIdOption(id)
    comment.map(comments => comments match {
      case Some(c) => Ok(views.html.comment(c))
      case None => Redirect(routes.CommentController.getAll)
    })
  }
}