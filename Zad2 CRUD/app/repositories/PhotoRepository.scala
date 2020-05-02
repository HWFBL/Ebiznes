package repositories

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PhotoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  val product = TableQuery[ProductTable]

  val photo = TableQuery[PhotoTable]

  def create(image: String, product: Long): Future[Photo] = db.run {
    //    (photo returning photo.map(_.id)) += Photo(0, photo, )
    (photo.map(k => (k.photo, k.product))
      returning photo.map(_.id)

      into { case ((photo, product), id) => Photo(id, photo, product) }
      ) += (image, product)
  }

  def delete(id: Long): Future[Unit] = db.run(photo.filter(_.id === id).delete).map( _ => ())

  def getById(id: Long): Future[Photo] = db.run {
    photo.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Photo]] = db.run {
    photo.filter(_.id === id).result.headOption
  }

  def list: Future[Seq[Photo]] = db.run {
    photo.result
  }

  def update(id: Long, new_photo: Photo): Future[Unit] = {
    val phToUpdate: Photo = new_photo.copy(id)
    db.run(photo.filter(_.id === id).update(phToUpdate)).map( _ => ())
  }

}
