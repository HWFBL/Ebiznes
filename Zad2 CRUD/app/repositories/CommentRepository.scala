package repositories

import models.Comment
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CommentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def content = column[String]("content")

    def * = (id, content) <> ((Comment.apply _).tupled, Comment.unapply)
  }

  val comment = TableQuery[CommentTable]
  
  def create(content: String): Future[Long] = db.run {
    (comment returning comment.map(_.id)) += Comment(0, content)
//    (comment.map(c => (c.content))
//      returning comment.map(_.id)
//      into ((content, id) => Comment(id, content))
//      ) += (content)
  }

  def list: Future[Seq[Comment]] = db.run {
    comment.result
  }

  def getById(id: Long): Future[Comment] = db.run {
    comment.filter(_.id == id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Comment]] = db.run {
    comment.filter(_.id == id).result.headOption
  }
}
