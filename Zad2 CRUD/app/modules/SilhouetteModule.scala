package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder, Signer}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.CookieHeaderEncoding
import repositories.{PasswordDAO, CustomerRepository}
import utils.auth.{CookieEnv, JsonErrorHandler, JwtEnv}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SilhouetteModule extends AbstractModule with ScalaModule {

  val _A_CRP = "authenticator-crypter"
  
  override def configure(): Unit = {
    bind[Silhouette[JwtEnv]].to[SilhouetteProvider[JwtEnv]]
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
    bind[JsonErrorHandler].toInstance(new JsonErrorHandler())
  }

  @Provides
  def providePasswordDAO(db: DatabaseConfigProvider): DelegableAuthInfoDAO[PasswordInfo] = new PasswordDAO(db)

  @Provides
  def provideCookieEnvironment(userService: CustomerRepository,
                               authenticatorService: AuthenticatorService[CookieAuthenticator],
                               eventBus: EventBus): Environment[CookieEnv] = {

    Environment[CookieEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  @Provides
  def provideJwtEnvironment(userService: CustomerRepository,
                            authenticatorService: AuthenticatorService[JWTAuthenticator],
                            eventBus: EventBus): Environment[JwtEnv] = {

    Environment[JwtEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  @Provides
  def provideCookieAuthenticatorService(@Named("authenticator-signer") signer: Signer,
                                        @Named(_A_CRP) crypter: Crypter,
                                        cookieHeaderEncoding: CookieHeaderEncoding,
                                        fingerprintGenerator: FingerprintGenerator,
                                        idGenerator: IDGenerator,
                                        configuration: Configuration,
                                        clock: Clock): AuthenticatorService[CookieAuthenticator] = {

    val config = CookieAuthenticatorSettings(
      cookieName = "auth",
      cookiePath = "/",
      cookieDomain = None,
      secureCookie = false,
      httpOnlyCookie = true,
      useFingerprinting = true,
      cookieMaxAge = None,
      authenticatorIdleTimeout = None,
      authenticatorExpiry = 12 hours
    )

    val authenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(config, None, signer, cookieHeaderEncoding, authenticatorEncoder, fingerprintGenerator, idGenerator, clock)
  }

  @Provides
  def provideJwtAuthenticatorService(@Named(_A_CRP) crypter: Crypter,
                                     idGenerator: IDGenerator,
                                     configuration: Configuration,
                                     clock: Clock): AuthenticatorService[JWTAuthenticator] = {

    val config = JWTAuthenticatorSettings(
      authenticatorExpiry = 12 hours,
      authenticatorIdleTimeout = None,
      issuerClaim = "mrustoja",
      requestParts = Some(Seq(RequestPart.Headers)),
      sharedSecret = "AutoGenerateSecreetKeyAutoGenerateSecreetKey"
    )

    val authenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)

    new JWTAuthenticatorService(config, None, authenticatorEncoder, idGenerator, clock)
  }


  @Provides
  def provideAuthInfoRepository(passwordDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordDAO)

  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = {
    PasswordHasherRegistry(passwordHasher)
  }

  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  @Provides
  @Named("authenticator-signer")
  def provideAuthenticatorSigner(configuration: Configuration): Signer = {
    val config = JcaSignerSettings("SecretKey")
    new JcaSigner(config)
  }

  @Provides
  @Named(_A_CRP)
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = JcaCrypterSettings("SecretKey")
    new JcaCrypter(config)
  }


}