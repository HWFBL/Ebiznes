package models

import slick.jdbc.SQLiteProfile.api._
import play.api.libs.json.Json

case class Product(id: Long, name: String, description: String, photo: Long, category: Int, price: Double, quantity: Int)

class ProductTable(tag: Tag) extends Table[Product](tag, "product") {


  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  /** The name column */
  def name = column[String]("name")

  /** The age column */
  def description = column[String]("description")

  def photo = column[Long]("photo")

  def category = column[Int]("category")

  def price = column[Double]("price")

  def quantity = column[Int]("quantity")


  def categoryFk = foreignKey("cat_fk", category, TableQuery[CategoryTable])(_.id)

  def photoFk = foreignKey("photo_fk", photo, TableQuery[PhotoTable])(_.id)


  /**
   * This is the tables default "projection".
   *
   * It defines how the columns are converted to and from the Person object.
   *
   * In this case, we are simply passing the id, name and page parameters to the Person case classes
   * apply and unapply methods.
   */
  def * = (id, name, description, photo, category, price, quantity) <> ((Product.apply _).tupled, Product.unapply)

}

object Product {
  implicit val productFormat = Json.format[Product]
}
