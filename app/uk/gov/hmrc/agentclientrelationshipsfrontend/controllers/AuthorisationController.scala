/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.IdentityVerificationConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.UrlHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.TimedOut
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.auth.*
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, OnlyRelative, RedirectUrl}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorisationController @Inject()(mcc: MessagesControllerComponents,
                                        identityVerificationConnector: IdentityVerificationConnector,
                                        cannotConfirmIdentityView: CannotConfirmIdentity,
                                        errorCannotViewRequestView: ErrorCannotViewRequest,
                                        notAuthorisedAsClientView: NotAuthorisedAsClient,
                                        ivTechDifficultiesView: IvTechDifficulties,
                                        ivLockedOutView: IvLockedOut,
                                        timedOutView: TimedOut)
                                       (implicit executionContext: ExecutionContext,
                                        appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport:

  def cannotViewRequest: Action[AnyContent] = Action.async:
    implicit request =>
      Future.successful(NotImplemented)
  //TODO should return errorCannotViewRequestView if the client type is known, otherwise notAuthorisedAsClientView

  def cannotConfirmIdentity(journeyId: Option[String], continueUrl: Option[RedirectUrl]): Action[AnyContent] = Action.async:
    implicit request =>
      val url = continueUrl.map(UrlHelper.validateRedirectUrl)
      journeyId
        .fold(
          Future.successful(Forbidden(cannotConfirmIdentityView(url)))
        )(id =>
          identityVerificationConnector
            .getIVResult(id)
            .map {
              case Some(TechnicalIssue) =>
                Forbidden(ivTechDifficultiesView())
              case Some(FailedMatching | FailedDirectorCheck | FailedIV | InsufficientEvidence) =>
                Forbidden(cannotConfirmIdentityView(url))
              case Some(UserAborted | TimedOut) => Redirect(routes.AuthorisationController.handleIVTimeout(continueUrl))
              case Some(LockedOut) => Redirect(routes.AuthorisationController.lockedOut)
              case _ => Forbidden(cannotConfirmIdentityView(url))
            }
        )

  def handleIVTimeout(continueUrl: Option[RedirectUrl]): Action[AnyContent] = Action.async:
    implicit request =>
      Future.successful(Forbidden(timedOutView(continueUrl.map(UrlHelper.validateRedirectUrl), isAgent = false)))

  def lockedOut: Action[AnyContent] = Action.async:
    implicit request =>
      Future.successful(Forbidden(ivLockedOutView()))