package com.github.BambooTuna.CommodityExchange.domain.asset

sealed trait Currency

case object JPY extends Currency
case object USD extends Currency
case object BTC extends Currency
