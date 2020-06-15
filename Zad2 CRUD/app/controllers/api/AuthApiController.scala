package controllers.api


import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, JWTAuthenticator}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import models.Customer
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents, MessagesAbstractController, MessagesControllerComponents}
import repositories.CustomerRepository
import utils.auth._

import scala.concurrent.{ExecutionContext, Future}

case class SignUp(firstName: String,
                  lastName: String,
                  email: String,
                  password: String
                 )

object SignUp {
  implicit val userFormat = Json.format[SignUp]
}

case class SignIn(email: String,
                  password: String
                 )

object SignIn {
  implicit val sigInFormat = Json.format[SignIn]
}


class AuthApiController @Inject()(cc: ControllerComponents,
                                            userRepository: CustomerRepository,

                                  errorHandler: JsonErrorHandler,
                                  silhouetteJwt: Silhouette[JwtEnv],
                                            credentialsProvider: CredentialsProvider)
                                           (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  val jwtAuthService: AuthenticatorService[JWTAuthenticator] = silhouetteJwt.env.authenticatorService

  def signUp() = silhouetteJwt.UnsecuredAction(parse.json).async { implicit request =>
    val body = request.body
    body.validate[SignUp].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      user => {
        userRepository.retrieve(LoginInfo(CredentialsProvider.ID, user.email))
          .flatMap {
            case Some(user) => Future.successful(BadRequest(Json.obj("message" -> "User already exist")))
            case None => {
              userRepository.create(user.firstName, user.lastName, user.email, user.password)
                .flatMap(jwtAuthService.create(_))
                .flatMap(jwtAuthService.init(_))
                .flatMap(token => Future.successful(Ok(Json.obj("message" -> "User created", "token" -> token))))
            }
          }
      }
    )
  }

  def signIn() = silhouetteJwt.UnsecuredAction(parse.json).async { implicit request =>
    val body = request.body
    body.validate[SignIn].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      credentials => {
        credentialsProvider.authenticate(credentials = Credentials(credentials.email, credentials.password))
          .flatMap { loginInfo =>
            userRepository.retrieve(loginInfo).flatMap {
              case Some(user) => jwtAuthService.create(loginInfo)
                .flatMap { authenticator =>
                  silhouetteJwt.env.eventBus.publish(LoginEvent(user, request))
                  silhouetteJwt.env.authenticatorService.init(authenticator).map { token =>
                    Ok(Json.obj("token" -> token))
                  }
                }
              case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
            }
          }.recover {
          case e: ProviderException =>
            Unauthorized(Json.obj("message" -> "Invalid credentials"))
        }
      }
    )
  }


  def me = silhouetteJwt.SecuredAction(errorHandler) { implicit request =>
    Ok(Json.toJson(request.identity))
  }
}
