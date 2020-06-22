package models

import play.api.libs.json.Json
import slick.jdbc.SQLiteProfile.api._

case class Comment(id: Long, content: String, product: Long)

class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {

  val _rat = TableQuery[RatingTable]
  val _prod = TableQuery[ProductTable]

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def content = column[String]("content")

  def product = column[Long]("product_id")


  def prodFk = foreignKey("prod_fk", product, _prod)(_.id)

  def * = (id, content, product) <> ((Comment.apply _).tupled, Comment.unapply)
}

object Comment {
  implicit val commentFormat = Json.format[Comment]
}


