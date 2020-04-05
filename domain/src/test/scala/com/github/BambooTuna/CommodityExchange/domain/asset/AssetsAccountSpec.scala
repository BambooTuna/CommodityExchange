package com.github.BambooTuna.CommodityExchange.domain.asset

import org.scalatest.{FreeSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures

import scala.util.Try

class AssetsAccountSpec extends FreeSpecLike with Matchers with ScalaFutures {

  val assetsAccountId: String = "test"

  "AssetsAccountSpec" - {
    "deposit isEmpty" in {
      val assetsAccount: AssetsAccount =
        AssetsAccount.create(assetsAccountId).assetDeposit(JPY, 100).get
      assetsAccount.assets shouldBe Set(Asset(JPY, 100))
    }

    "deposit" in {
      val assetsAccount: AssetsAccount =
        AssetsAccount
          .create(assetsAccountId)
          .assetDeposit(JPY, 100)
          .get
          .assetDeposit(JPY, 100)
          .get
      assetsAccount.assets shouldBe Set(Asset(JPY, 200))
    }

    "deposit amount should larger than 0" in {
      val tryAssetsAccount: Try[AssetsAccount] =
        AssetsAccount.create(assetsAccountId).assetDeposit(JPY, 0)
      the[IllegalArgumentException] thrownBy tryAssetsAccount.get
    }

    "withdraw with negative balance" in {
      val tryAssetsAccount: Try[AssetsAccount] =
        AssetsAccount.create(assetsAccountId).assetWithdraw(JPY, 100)
      the[IllegalArgumentException] thrownBy tryAssetsAccount.get
    }

    "withdraw amount should larger than 0" in {
      val tryAssetsAccount: Try[AssetsAccount] =
        AssetsAccount.create(assetsAccountId).assetWithdraw(JPY, 0)
      the[IllegalArgumentException] thrownBy tryAssetsAccount.get
    }

    "balance currency isEmpty" in {
      val assetsAccount: AssetsAccount =
        AssetsAccount.create(assetsAccountId)
      assetsAccount.assetBalance(JPY) shouldBe Asset(JPY, 0)
    }

    "balance" in {
      val assetsAccount: AssetsAccount =
        AssetsAccount.create(assetsAccountId).assetDeposit(JPY, 100).get
      assetsAccount.assetBalance(JPY) shouldBe Asset(JPY, 100)
    }

  }

}
