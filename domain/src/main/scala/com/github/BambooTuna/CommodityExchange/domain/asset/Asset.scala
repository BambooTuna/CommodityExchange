package com.github.BambooTuna.CommodityExchange.domain.asset

import scala.util.Try

case class Asset(currency: Currency, balance: BigDecimal) {
  require(balance >= 0, "残高がマイナスになってしまいます")

  override def equals(obj: Any): Boolean = obj match {
    case Asset(c, _) => this.currency == c
    case _           => false
  }

  def deposit(amount: BigDecimal): Try[Asset] = Try {
    copy(balance = this.balance + amount)
  }

  def withdraw(amount: BigDecimal): Try[Asset] = Try {
    copy(balance = this.balance - amount)
  }

}

object Asset {
  def empty(currency: Currency): Asset = Asset(currency, 0)
}
