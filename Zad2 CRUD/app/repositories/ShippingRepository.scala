package repositories

import javax.inject.{Inject, Singleton}
import models.Shipping
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ShippingRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
   val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  @Singleton
  class ShippingTable(tag: Tag) extends Table[Shipping](tag, "shipping") {
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def house_number = column[String]("house_number")
    def city = column[String]("city")
    def zip_code = column[String]("zip_code")
    
    def * = (id, street, house_number, city, zip_code) <> ((Shipping.apply _).tupled, Shipping.unapply)
  }
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
}
