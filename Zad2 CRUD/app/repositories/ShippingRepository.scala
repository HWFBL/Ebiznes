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
  
  def create(street: String, houseNumber: String, city: String, zipCode: String) = db.run {
    (shipping.map(u => (u.street, u.houseNumber, u.city, u.zipCode))
      returning shipping.map(_.id)

      into {case ((street, houseNumber, city, zipCode), id) => Shipping(id, street, houseNumber, city, zipCode)}
      ) += (street,houseNumber, city, zipCode)
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

  def update(id: Long, newShip: Shipping): Future[Unit] = {
    val shipToUpdate: Shipping = newShip.copy(id)
    db.run(shipping.filter(_.id === id).update(shipToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(shipping.filter(_.id === id).delete).map(_ => ())



}
