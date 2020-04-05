package com.github.BambooTuna.CommodityExchange.adaptor.json

import com.github.BambooTuna.AkkaServerSupport.core.serializer.JsonRecodeSerializer
import com.github.BambooTuna.CommodityExchange.adaptor.validate.EMail
import com.github.BambooTuna.CommodityExchange.domain.SystemSettings
import com.github.BambooTuna.CommodityExchange.domain.account.{
  EncryptedPasswordImpl,
  UserCredentialsImpl
}

case class SignUpRequestJsonImpl(mail: String, pass: String) {
  require(mail.nonEmpty, "mail is empty")
  require(pass.nonEmpty, "pass is empty")

  require(EMail.isValid(mail), "mail is inValid")
}

object SignUpRequestJsonImpl {
  implicit val js =
    new JsonRecodeSerializer[SignUpRequestJsonImpl, UserCredentialsImpl] {
      override def toRecode(json: SignUpRequestJsonImpl): UserCredentialsImpl =
        UserCredentialsImpl(
          id = SystemSettings.generateId(),
          signinId = json.mail,
          signinPass =
            EncryptedPasswordImpl(json.pass).changeEncryptedPass(json.pass),
          activated = false)
    }
}
