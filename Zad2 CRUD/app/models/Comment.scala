package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Comment(id: Long, content: String )

class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def content = column[String]("content")

  def * = (id, content) <> ((Comment.apply _).tupled, Comment.unapply)
}

object Comment {
  implicit val commentFormat = Json.format[Comment]
}


