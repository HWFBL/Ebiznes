package repositories

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ShippingRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
   val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


   val shipping = TableQuery[ShippingTable]
  
  def create(street: String, house_number: String, city: String, zip_code: String) = db.run {
    (shipping.map(u => (u.street, u.house_number, u.city, u.zip_code))
      returning shipping.map(_.id)

      into {case ((street, house_number, city, zip_code), id) => Shipping(id, street, house_number, city, zip_code)}
      ) += (street,house_number, city, zip_code)
  }



  def list(): Future[Seq[Shipping]] = db.run {
    shipping.result
  }

  def getById(id: Long): Future[Shipping] = db.run {
    shipping.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Shipping]] = db.run {
    shipping.filter(_.id === id).result.headOption
  }

  def update(id: Long, new_ship: Shipping): Future[Unit] = {
    val shipToUpdate: Shipping = new_ship.copy(id)
    db.run(shipping.filter(_.id === id).update(shipToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(shipping.filter(_.id === id).delete).map(_ => ())



}
