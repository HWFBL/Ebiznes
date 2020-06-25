package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder, Signer}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.impl.providers.oauth2.GoogleProvider
import com.mohiva.play.silhouette.impl.providers.state.{CsrfStateItemHandler, CsrfStateSettings}
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, DefaultSocialStateHandler, OAuth2Info, OAuth2Settings, SocialProviderRegistry, SocialStateHandler}
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
import play.api.libs.ws.WSClient
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.CookieHeaderEncoding
import repositories.authentication.{OAuth2InfoDAO, PasswordDAO}
import repositories.CustomerRepository
import utils.auth.{CookieEnv, JsonErrorHandler, JwtEnv}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SilhouetteModule extends AbstractModule with ScalaModule {

  
  
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
                                        @Named("authenticator-crypter") crypter: Crypter,
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
  def provideJwtAuthenticatorService(@Named("authenticator-crypter") crypter: Crypter,
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
  def provideAuthInfoRepository(passwordDAO: DelegableAuthInfoDAO[PasswordInfo], OAuth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info] ): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordDAO, OAuth2InfoDAO)

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
  def provideOAuth2InfoDAO(dbConfig: DatabaseConfigProvider): DelegableAuthInfoDAO[OAuth2Info] = {
    new OAuth2InfoDAO(dbConfig)
  }

  @Provides
  def provideSocialProviderRegistry(googleProvider: GoogleProvider): SocialProviderRegistry = {
    SocialProviderRegistry(Seq(googleProvider))
  }

  @Provides
  def provideGoogleProvider(httpLayer: HTTPLayer,
                            socialStateHandler: SocialStateHandler,
                            configuration: Configuration): GoogleProvider = {
    val config = OAuth2Settings(
      authorizationURL = Option("https://accounts.google.com/o/oauth2/auth"),
      accessTokenURL = "https://accounts.google.com/o/oauth2/token",
      redirectURL=Option("http://localhost:9000/api/auth/oauth/google"),
      clientID="158074163261-mffpub2nbchnqehdh6jheu99ci8rqvjr.apps.googleusercontent.com",
      clientSecret="TIflbA1-8y7FKa4BhH5jqWAy",
      scope=Option("profile email")
    )
    new GoogleProvider(httpLayer, socialStateHandler, config)
  }

  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = {
    new PlayHTTPLayer(client)
  }

  @Provides
  def provideSocialStateHandler(@Named("social-state-signer") signer: Signer,
                                csrfStateItemHandler: CsrfStateItemHandler): SocialStateHandler = {
    new DefaultSocialStateHandler(Set(csrfStateItemHandler), signer)
  }

  @Provides
  @Named("social-state-signer")
  def provideSocialStateSigner(configuration: Configuration): Signer = {
    val config: JcaSignerSettings = JcaSignerSettings("SecretKey")
    new JcaSigner(config)
  }

  @Provides
  def provideCsrfStateItemHandler(idGenerator: IDGenerator,
                                  @Named("csrf-state-item-signer") signer: Signer,
                                  configuration: Configuration): CsrfStateItemHandler = {
    val settings = CsrfStateSettings(
      cookieName="OAuth2State",
      secureCookie = false
    )
    new CsrfStateItemHandler(settings, idGenerator, signer)
  }

  @Provides
  @Named("csrf-state-item-signer")
  def provideCSRFStateItemSigner(configuration: Configuration): Signer = {
    val config: JcaSignerSettings = JcaSignerSettings("SecretKey")

    new JcaSigner(config)
  }

  @Provides
  @Named("authenticator-signer")
  def provideAuthenticatorSigner(configuration: Configuration): Signer = {
    val config = JcaSignerSettings("SecretKey")
    new JcaSigner(config)
  }

  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = JcaCrypterSettings("SecretKey")
    new JcaCrypter(config)
  }


}