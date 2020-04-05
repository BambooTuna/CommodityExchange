package com.github.BambooTuna.CommodityExchange.domain

object SystemSettings {
  def generateId(): String =
    java.util.UUID.randomUUID.toString.replaceAll("-", "")

}
