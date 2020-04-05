package com.github.BambooTuna.CommodityExchange.adaptor.usecase

import com.github.BambooTuna.AkkaServerSupport.authentication.dao.UserCredentialsDao
import com.github.BambooTuna.AkkaServerSupport.authentication.useCase.EmailAuthenticationUseCase
import com.github.BambooTuna.AkkaServerSupport.core.session.StorageStrategy
import com.github.BambooTuna.CommodityExchange.adaptor.usecase.mail.EmailSettings
import com.github.BambooTuna.CommodityExchange.domain.account.UserCredentialsImpl
import monix.eval.Task

class EmailAuthenticationUseCaseImpl(
    val userCredentialsDao: UserCredentialsDao[UserCredentialsImpl],
    val cacheStorage: StorageStrategy[String, String],
    emailSettings: EmailSettings
) extends EmailAuthenticationUseCase[UserCredentialsImpl] {

  override protected def sendActivateCodeTo(mail: String,
                                            code: String): Task[Unit] = {
    val customEmail =
      emailSettings.email
        .to(mail)
        .withSubject(emailSettings.emailContents.activateCodeSubject)
        .withPlainText(
          emailSettings.emailContents.generateActivateCodeContent(code))
        .buildEmail()
    Task { emailSettings.mailer.sendMail(customEmail) }
  }

  override protected def sendInitializationCodeTo(mail: String,
                                                  code: String): Task[Unit] = {
    val customEmail =
      emailSettings.email
        .to(mail)
        .withSubject(emailSettings.emailContents.initializationCodeSubject)
        .withPlainText(
          emailSettings.emailContents.generateInitializationCodeContent(code))
        .buildEmail()
    Task { emailSettings.mailer.sendMail(customEmail) }
  }

  override protected def sendNewPlainPasswordTo(
      mail: String,
      newPlainPassword: String): Task[Unit] = {
    val customEmail =
      emailSettings.email
        .to(mail)
        .withSubject(emailSettings.emailContents.newPassNotificationSubject)
        .withPlainText(emailSettings.emailContents
          .generateNewPassNotificationContent(newPlainPassword))
        .buildEmail()
    Task { emailSettings.mailer.sendMail(customEmail) }
  }
}
