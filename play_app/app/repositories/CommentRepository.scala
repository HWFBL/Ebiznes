package repositories

import models._
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CommentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._



  val comment = TableQuery[CommentTable]
  
  def create(content: String, product: Long, rating: Long) = db.run {
    (comment returning comment.map(_.id)) += Comment(0, content, product, rating)
//    (comment.map(c => (c.content, c.product, c.rating))
//      returning comment.map(_.id)
//      into { case ((content, product, rating), id) => Comment(id, content, product, rating) }
//      ) += (content, product, rating)
  }

  def list: Future[Seq[Comment]] = db.run {
    comment.result
  }

  def getById(id: Long): Future[Comment] = db.run {
    comment.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Comment]] = db.run {
    comment.filter(_.id === id).result.headOption
  }
  def update(id: Long, newComment: Comment): Future[Unit] = {
    val comToUpdate: Comment = newComment.copy(id)
    db.run(comment.filter(_.id === id).update(comToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(comment.filter(_.id === id).delete).map( _ => ())

}
