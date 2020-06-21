package utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import models.Customer

trait CookieEnv extends Env {
  type I = Customer
  type A = CookieAuthenticator
}

trait JwtEnv extends Env {
  type I = Customer
  type A = JWTAuthenticator
}