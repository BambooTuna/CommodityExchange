package com.github.BambooTuna.CommodityExchange.adaptor.usecase.mail

import com.typesafe.config.Config
import org.simplejavamail.api.email.EmailPopulatingBuilder
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

case class EmailSettings(
    email: EmailPopulatingBuilder,
    mailer: Mailer,
    emailContents: EmailContents
)

object EmailSettings {
  def fromConfig(config: Config): EmailSettings =
    EmailSettings(
      email = EmailBuilder
        .startingBlank()
        .from(config.getString("mail.from")),
      mailer = MailerBuilder
        .withSMTPServer(
          config.getString("mail.host"),
          config.getString("mail.port").toInt,
          config.getString("mail.username"),
          config.getString("mail.password")
        )
        .withTransportStrategy(TransportStrategy.SMTPS)
        .buildMailer(),
      emailContents = EmailContents.fromConfig(config)
    )
}
