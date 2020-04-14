package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  private class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def category = column[Int]("category")

    def category_fk = foreignKey("cat_fk",category, cat)(_.id)


    def * = (id, name, description, category) <> ((Product.apply _).tupled, Product.unapply)

  }



  import categoryRepository.CategoryTable

  private val product = TableQuery[ProductTable]

  private val cat = TableQuery[CategoryTable]


  def create(name: String, description: String, category: Int): Future[Product] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (product.map(p => (p.name, p.description,p.category))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning product.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into {case ((name,description,category),id) => Product(id,name, description,category)}
      // And finally, insert the product into the database
      ) += (name, description,category)
  }

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getByCategory(category_id: Int): Future[Seq[Product]] = db.run {
    product.filter(_.category === category_id).result
  }

  def getById(id: Long): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def getByCategories(category_ids: List[Int]): Future[Seq[Product]] = db.run {
    product.filter(_.category inSet category_ids).result
  }

  def delete(id: Long): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_product: Product): Future[Unit] = {
    val productToUpdate: Product = new_product.copy(id)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}
