package com.github.BambooTuna.CommodityExchange.adaptor.usecase.mail

import com.typesafe.config.Config

case class EmailContents(
    activateCodeSubject: String,
    activateCodeContent: String,
    initializationCodeSubject: String,
    initializationCodeContent: String,
    newPassNotificationSubject: String,
    newPassNotificationContent: String,
) {
  require(activateCodeContent.contains("{{ code }}"))
  require(initializationCodeContent.contains("{{ code }}"))
  require(newPassNotificationContent.contains("{{ pass }}"))

  def generateActivateCodeContent(code: String): String =
    activateCodeContent.replace("{{ code }}", code)
  def generateInitializationCodeContent(code: String): String =
    initializationCodeContent.replace("{{ code }}", code)
  def generateNewPassNotificationContent(pass: String): String =
    newPassNotificationContent.replace("{{ pass }}", pass)
}

object EmailContents {
  def fromConfig(config: Config): EmailContents =
    EmailContents(
      activateCodeSubject =
        config.getString("mail.contents.activate_code.subject"),
      activateCodeContent =
        config.getString("mail.contents.activate_code.content"),
      initializationCodeSubject =
        config.getString("mail.contents.initialization_code.subject"),
      initializationCodeContent =
        config.getString("mail.contents.initialization_code.content"),
      newPassNotificationSubject =
        config.getString("mail.contents.pass_notification.subject"),
      newPassNotificationContent =
        config.getString("mail.contents.pass_notification.content")
    )
}
