package utils.auth

import com.atlassian.jwt.core.http.auth.JwtAuthenticator
import com.mohiva.play.silhouette.api.{Authenticator, Authorization}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import models.{Customer, UserRoles}
import models.UserRoles.UserRole
import play.api.mvc.Request

import scala.concurrent.Future


case class RoleJWTAuthorization(role: UserRole) extends Authorization[Customer, JWTAuthenticator] {
  def isAuthorized[B](user: Customer, authenticator: JWTAuthenticator)(implicit request: Request[B]) = {
    if (user.roles == UserRoles.Admin) {
      Future.successful(true)
    } else {
      Future.successful(user.roles == role)
    }
  }
}

case class RoleCookieAuthorization(role: UserRole) extends Authorization[Customer, CookieAuthenticator] {
  def isAuthorized[B](user: Customer, authenticator: CookieAuthenticator)(implicit request: Request[B]) = {
    if (user.roles == UserRoles.Admin) {
      Future.successful(true)
    } else {
      Future.successful(user.roles == role)
    }
  }
}