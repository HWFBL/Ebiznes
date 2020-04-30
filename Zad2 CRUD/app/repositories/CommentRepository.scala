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
  
  def create(content: String): Future[Comment] = db.run {
//    (comment returning comment.map(_.id)) += Comment(0, content)
    (comment.map(c => (c.content))
      returning comment.map(_.id)
      into ((content, id) => Comment(id, content))
      ) += (content)
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
}
