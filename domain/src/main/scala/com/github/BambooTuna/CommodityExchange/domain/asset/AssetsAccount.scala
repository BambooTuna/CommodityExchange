package com.github.BambooTuna.CommodityExchange.domain.asset

import scala.util.Try

case class AssetsAccount(assetsAccountId: String, assets: Set[Asset]) {

  def assetBalance(currency: Currency): Asset = {
    this.assets
      .find(_.currency == currency)
      .getOrElse(Asset.empty(currency))
  }

  def assetDeposit(amount: Asset): Try[AssetsAccount] =
    Try {
      val assets =
        (this.assets + Asset.empty(amount.currency))
          .collect {
            case asset: Asset if asset.currency == amount.currency =>
              asset.deposit(amount.balance).get
            case other => other
          }
      copy(assets = assets)
    }

  def assetWithdraw(amount: Asset): Try[AssetsAccount] = Try {
    val assets =
      (this.assets + Asset.empty(amount.currency))
        .collect {
          case asset: Asset if asset.currency == amount.currency =>
            asset.withdraw(amount.balance).get
          case other => other
        }
    copy(assets = assets)
  }

}

object AssetsAccount {
  def create(assetsAccountId: String): AssetsAccount =
    AssetsAccount(assetsAccountId, Set.empty)
}
