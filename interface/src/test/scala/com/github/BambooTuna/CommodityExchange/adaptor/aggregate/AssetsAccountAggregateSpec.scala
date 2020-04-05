package com.github.BambooTuna.CommodityExchange.adaptor.aggregate

import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.{BeforeAndAfterAll, FreeSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.github.BambooTuna.CommodityExchange.adaptor.aggregate.AssetsAccountAggregate.Protocol._
import com.github.BambooTuna.CommodityExchange.domain.asset.{Asset, JPY}

import scala.concurrent.duration._

class AssetsAccountAggregateSpec
    extends TestKit(ActorSystem("AssetsAccountAggregateSpec"))
    with FreeSpecLike
    with Matchers
    with ScalaFutures
    with BeforeAndAfterAll
    with ImplicitSender {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  implicit val timeout: Timeout = Timeout(5.seconds)

  val assetsAccountId: String = "test"

  "AssetsAccountAggregateSpec" - {
    "general" in {
      val actor: ActorRef = system.actorOf(AssetsAccountAggregate.props)

      actor ! OpenAssetsAccountRequest(assetsAccountId)
      expectMsg(OpenAssetsAccountResponseSuccess)

      actor ! DepositRequest(assetsAccountId, Asset(JPY, 100))
      expectMsg(DepositResponseSuccess)

      actor ! WithdrawRequest(assetsAccountId, Asset(JPY, 50))
      expectMsg(WithdrawResponseSuccess)

      actor ! GetBalanceRequest(assetsAccountId, JPY)
      expectMsg(GetBalanceResponseSuccess(50))
    }

    "withdraw failed" in {
      val actor: ActorRef = system.actorOf(AssetsAccountAggregate.props)

      actor ! OpenAssetsAccountRequest(assetsAccountId)
      expectMsg(OpenAssetsAccountResponseSuccess)

      actor ! WithdrawRequest(assetsAccountId, Asset(JPY, 100))
      expectMsgType[WithdrawResponseFailed]
    }
  }

}
