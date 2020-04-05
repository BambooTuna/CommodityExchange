package com.github.BambooTuna.CommodityExchange.adaptor.json

import com.github.BambooTuna.AkkaServerSupport.authentication.json.SignInRequestJson
import com.github.BambooTuna.CommodityExchange.adaptor.validate.EMail

case class SignInRequestJsonImpl(mail: String, pass: String)
    extends SignInRequestJson {
  require(mail.nonEmpty, "mail is empty")
  require(pass.nonEmpty, "pass is empty")

  require(EMail.isValid(mail), "mail is inValid")

  override val signInId: String = mail
  override val signInPass: String = pass
}
