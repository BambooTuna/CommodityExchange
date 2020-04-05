package com.github.BambooTuna.CommodityExchange.apiServer

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{DELETE, GET, POST, PUT}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Rejection, RejectionHandler, Route}
import akka.stream.ActorMaterializer
import com.github.BambooTuna.AkkaServerSupport.authentication.error.{
  AccountAlreadyExistsError,
  AccountNotFoundError,
  ActivateAccountError,
  AlreadyActivatedError,
  AuthenticationCustomError,
  CSRFTokenForbiddenError,
  InvalidActivateCodeError,
  InvalidInitializationCodeError,
  LinkedAccountAlreadyExistsError,
  LinkedAccountNotFoundError,
  OAuth2CustomError,
  ParseAccessTokenAcquisitionResponseError,
  ParseParameterFailedError,
  ParseToRegisterCommandError,
  ParseToSignInCommandError,
  SignInForbiddenError
}
import com.github.BambooTuna.AkkaServerSupport.core.model.ServerConfig
import com.github.BambooTuna.AkkaServerSupport.core.router.{
  RouteController,
  Router,
  route
}
import com.github.BambooTuna.CommodityExchange.adaptor.json.ErrorResponseJson
import org.slf4j.{Logger, LoggerFactory}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext

class RouteControllerImpl(serverConfig: ServerConfig)(
    implicit system: ActorSystem,
    mat: ActorMaterializer,
    executor: ExecutionContext)
    extends Component
    with RouteController {

  private val logger: Logger = LoggerFactory.getLogger(getClass)
  val apiVersion = "v2"

  val customRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case e: OAuth2CustomError =>
          e match {
            case ParseParameterFailedError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson(
                  "クエリパラメーターの形式が無効です"))
            case ParseAccessTokenAcquisitionResponseError =>
              complete(
                StatusCodes.Forbidden -> ErrorResponseJson("AccessToken"))
            case CSRFTokenForbiddenError =>
              complete(StatusCodes.Forbidden -> ErrorResponseJson("CSRF"))
            case LinkedAccountAlreadyExistsError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson("すでに登録されています"))
            case LinkedAccountNotFoundError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson("アカウントが見つかりません"))
            case ParseToRegisterCommandError(message) =>
              complete(StatusCodes.BadRequest -> ErrorResponseJson(message))
            case ParseToSignInCommandError(message) =>
              complete(StatusCodes.BadRequest -> ErrorResponseJson(message))
          }
        case e: AuthenticationCustomError =>
          e match {
            case AccountAlreadyExistsError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson("すでに登録されています"))
            case SignInForbiddenError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson(
                  "メールアドレスかパスワードが間違えています"))
            case AccountNotFoundError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson("アカウントが見つかりません"))
            case ActivateAccountError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson(
                  "アカウントをアクティベート出来ません"))
            case AlreadyActivatedError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson(
                  "アカウントがすでにアクティベートされています"))
            case InvalidActivateCodeError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson("アクティベートコードが無効です"))
            case InvalidInitializationCodeError =>
              complete(
                StatusCodes.BadRequest -> ErrorResponseJson("初期化コードが無効です"))
          }
        case e: akka.http.scaladsl.server.ValidationRejection =>
          complete(StatusCodes.BadRequest -> ErrorResponseJson(e.message))
        case e: akka.http.scaladsl.server.MalformedRequestContentRejection =>
          complete(StatusCodes.BadRequest -> ErrorResponseJson(e.message))
        case akka.http.scaladsl.server.AuthorizationFailedRejection =>
          complete(StatusCodes.Unauthorized)
        case e: Rejection =>
          complete(
            StatusCodes.BadRequest -> ErrorResponseJson(s"Unknown Error: $e"))
      }
      .result()

  override def toRoutes: Route =
    corsSupport.corsHandler {
      handleExceptions(defaultExceptionHandler(logger)) {
        handleRejections(customRejectionHandler) {
          pathPrefix(apiVersion) {
            (
              authenticationCodeCycleRoute(
                monix.execution.Scheduler.Implicits.global) +
                accountCycleRoute(monix.execution.Scheduler.Implicits.global)
            ).create
          }
        }
      }
    }

  def authenticationCodeCycleRoute(implicit s: Scheduler): Router = {
    Router(
      route(PUT, "activate", authenticationController.issueActivateCodeRoute),
      route(GET,
            "activate" / Segment,
            authenticationController.activateAccountRoute),
      route(POST, "init", authenticationController.tryInitializationRoute),
      route(GET, "init" / Segment, authenticationController.initAccountPassword)
    )
  }

  def accountCycleRoute(implicit s: Scheduler): Router =
    Router(
      route(POST, "signup", authenticationController.signUpRoute),
      route(POST, "signin", authenticationController.signInRoute),
      route(GET, "health", authenticationController.healthCheck),
      route(DELETE, "logout", authenticationController.logout)
    )

}
