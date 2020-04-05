package com.github.BambooTuna.CommodityExchange.adaptor.json

import com.github.BambooTuna.AkkaServerSupport.authentication.json.PasswordInitializationRequestJson

case class PasswordInitializationRequestJsonImpl(mail: String)
    extends PasswordInitializationRequestJson {
  override val signInId: String = mail
}
