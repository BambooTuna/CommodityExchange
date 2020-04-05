package com.github.BambooTuna.CommodityExchange.adaptor.aggregate

import akka.actor.Props
import akka.persistence.{
  PersistentActor,
  RecoveryCompleted,
  SaveSnapshotSuccess,
  SnapshotOffer
}
import com.github.BambooTuna.CommodityExchange.adaptor.aggregate.AssetsAccountAggregate.Protocol._
import com.github.BambooTuna.CommodityExchange.domain.asset.{
  Asset,
  AssetsAccount,
  Currency
}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class AssetsAccountAggregate extends PersistentActor {
  context.setReceiveTimeout(120.seconds)
  override def persistenceId: String = self.path.name

  private var stateOpt: Option[AssetsAccount] = None

  override def receiveRecover: Receive = {
    case event: AssetsAccountOpened =>
      stateOpt = Some(applyState(event))
    case AssetsAccountDeposited(_, deposit) =>
      stateOpt = stateOpt.flatMap(_.assetDeposit(deposit).toOption)
    case AssetsAccountWithdrawn(_, withdraw) =>
      stateOpt = stateOpt.flatMap(_.assetWithdraw(withdraw).toOption)
    case SnapshotOffer(_, _state: AssetsAccount) =>
      stateOpt = Some(_state)
    case SaveSnapshotSuccess(metadata) =>
      println(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case RecoveryCompleted =>
      println(s"Recovery completed: $persistenceId")
  }

  override def receiveCommand: Receive = {
    case OpenAssetsAccountRequest(assetsAccountId) if stateOpt.isEmpty =>
      persist(AssetsAccountOpened(assetsAccountId)) { event =>
        stateOpt = Some(applyState(event))
        sender() ! OpenAssetsAccountResponseSuccess
        tryToSaveSnapshot()
      }
    case GetBalanceRequest(assetsAccountId, currency)
        if equalsId(assetsAccountId) =>
      foreachState { state =>
        sender() ! GetBalanceResponseSuccess(
          state.assetBalance(currency).balance)
      }
    case DepositRequest(assetsAccountId, deposit)
        if equalsId(assetsAccountId) =>
      persist(AssetsAccountDeposited(assetsAccountId, deposit)) { event =>
        mapState(_.assetDeposit(deposit)) match {
          case Failure(exception) =>
            sender() ! DepositResponseFailed(exception.getMessage)
          case Success(newState) =>
            stateOpt = Some(newState)
            sender() ! DepositResponseSuccess
            tryToSaveSnapshot()
        }
      }
    case WithdrawRequest(assetsAccountId, withdraw)
        if equalsId(assetsAccountId) =>
      persist(AssetsAccountWithdrawn(assetsAccountId, withdraw)) { event =>
        mapState(_.assetWithdraw(withdraw)) match {
          case Failure(exception) =>
            sender() ! WithdrawResponseFailed(exception.getMessage)
          case Success(newState) =>
            stateOpt = Some(newState)
            sender() ! WithdrawResponseSuccess
            tryToSaveSnapshot()
        }
      }
    case SaveSnapshotSuccess(metadata) =>
      println(s"receiveCommand: SaveSnapshotSuccess succeeded: $metadata")
  }

  private def equalsId(requestId: String): Boolean =
    stateOpt match {
      case None =>
        throw new IllegalStateException(
          s"Invalid state: requestId = $requestId")
      case Some(state) =>
        state.assetsAccountId == requestId
    }

  private def applyState(event: AssetsAccountOpened): AssetsAccount =
    AssetsAccount.create(event.assetsAccountId)

  private def foreachState(f: AssetsAccount => Unit): Unit =
    stateOpt.foreach(f)

  private def mapState(
      f: AssetsAccount => Try[AssetsAccount]): Try[AssetsAccount] = {
    for {
      state <- Try { stateOpt.get }
      newState <- f(state)
    } yield newState
  }

  private def tryToSaveSnapshot(): Unit =
    if (lastSequenceNr % 5 == 0) {
      println(s"SaveSnapshot: lastSequenceNr=$lastSequenceNr")
      foreachState(saveSnapshot)
    }

}

object AssetsAccountAggregate {

  def props: Props = Props(new AssetsAccountAggregate())

  final val AggregateName = "AssetsAccount"

  object Protocol {
    sealed trait AssetsAccountEvent {
      val assetsAccountId: String
    }
    case class AssetsAccountOpened(assetsAccountId: String)
        extends AssetsAccountEvent
    case class AssetsAccountDeposited(assetsAccountId: String, deposit: Asset)
        extends AssetsAccountEvent
    case class AssetsAccountWithdrawn(assetsAccountId: String, withdraw: Asset)
        extends AssetsAccountEvent

    sealed trait AssetsAccountCommandRequest {
      val assetsAccountId: String
    }
    case class OpenAssetsAccountRequest(assetsAccountId: String)
        extends AssetsAccountCommandRequest
    case class GetBalanceRequest(assetsAccountId: String, currency: Currency)
        extends AssetsAccountCommandRequest
    case class DepositRequest(assetsAccountId: String, deposit: Asset)
        extends AssetsAccountCommandRequest
    case class WithdrawRequest(assetsAccountId: String, withdraw: Asset)
        extends AssetsAccountCommandRequest

    sealed trait AssetsAccountResponse
    case object OpenAssetsAccountResponseSuccess extends AssetsAccountResponse

    case class GetBalanceResponseSuccess(balance: BigDecimal)
        extends AssetsAccountResponse

    case object DepositResponseSuccess extends AssetsAccountResponse
    case class DepositResponseFailed(message: String)
        extends AssetsAccountResponse

    case object WithdrawResponseSuccess extends AssetsAccountResponse
    case class WithdrawResponseFailed(message: String)
        extends AssetsAccountResponse

    case class NotHandledMessage(message: Any) extends AssetsAccountResponse
  }

}
