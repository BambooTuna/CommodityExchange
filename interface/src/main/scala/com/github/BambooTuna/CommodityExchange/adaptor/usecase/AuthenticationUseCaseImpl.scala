package com.github.BambooTuna.CommodityExchange.adaptor.usecase

import com.github.BambooTuna.AkkaServerSupport.authentication.dao.UserCredentialsDao
import com.github.BambooTuna.AkkaServerSupport.authentication.useCase.AuthenticationUseCase
import com.github.BambooTuna.CommodityExchange.adaptor.json.{
  SignInRequestJsonImpl,
  SignUpRequestJsonImpl
}
import com.github.BambooTuna.CommodityExchange.domain.account.UserCredentialsImpl

class AuthenticationUseCaseImpl(
    val userCredentialsDao: UserCredentialsDao[UserCredentialsImpl])
    extends AuthenticationUseCase[SignUpRequestJsonImpl,
                                  SignInRequestJsonImpl,
                                  UserCredentialsImpl]
