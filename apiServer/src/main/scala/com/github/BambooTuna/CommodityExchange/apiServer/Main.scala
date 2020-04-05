package com.github.BambooTuna.CommodityExchange.apiServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.BambooTuna.AkkaServerSupport.core.model.ServerConfig

import scala.concurrent.ExecutionContextExecutor

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("commodity-exchange-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val serverConfig: ServerConfig =
    ServerConfig(
      system.settings.config.getString("boot.server.host"),
      system.settings.config.getString("boot.server.port").toInt
    )

  val r = new RouteControllerImpl(serverConfig)

  val bindingFuture =
    Http().bindAndHandle(r.toRoutes, serverConfig.host, serverConfig.port)

  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
