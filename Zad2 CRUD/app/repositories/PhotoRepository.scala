package repositories

import javax.inject.{Inject, Singleton}
import models.Photo
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PhotoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PhotoTable(tag: Tag) extends Table[Photo](tag, "photo") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def photo = column[Array[Byte]]("photo")

    def * = (id, photo) <> ((Photo.apply _).tupled, Photo.unapply)

  }

  import productRepository.ProductTable

  val product = TableQuery[ProductTable]

  val photo = TableQuery[PhotoTable]

  def create(image: Array[Byte]): Future[Photo] = db.run {
    //    (photo returning photo.map(_.id)) += Photo(0, photo, )
    (photo.map(k => (k.photo))
      returning photo.map(_.id)

      into { case ((photo), id) => Photo(id, photo) }
      ) += (image)
  }

  def getById(id: Long): Future[Photo] = db.run {
    photo.filter(_.id == id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Photo]] = db.run {
    photo.filter(_.id == id).result.headOption
  }

  def list: Future[Seq[Photo]] = db.run {
    photo.result
  }

}
