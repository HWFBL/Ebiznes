package models

import play.api.libs.json._
import slick.jdbc.SQLiteProfile.api._

case class Category(id: Int, name: String)

class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
}

object Category {
  implicit val categoryFormat = Json.format[Category]
}

