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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client

import com.google.inject.{Inject, Singleton}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentClientRelationshipsService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConfirmationPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmationController @Inject()(mcc: MessagesControllerComponents,
                                       actions: Actions,
                                       agentClientRelationshipsService: AgentClientRelationshipsService,
                                       confirmationPage: ConfirmationPage
                                         )
                                      (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport with Logging:

  def show: Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      request.journey.journeyComplete match {
        case Some(invitationId) =>
          agentClientRelationshipsService.getAuthorisationRequestForClient(invitationId = invitationId).map {
            case Some(authorisationRequestInfo) =>
              Ok(confirmationPage(authorisationRequestInfo))
            case None =>
              throw new RuntimeException(s"Authorisation request not found for invitationId: $invitationId")
          }
        case _ =>
          logger.warn(s"Redirecting to MYTA as client journey is not valid for confirmation page - ${request.journey}")
          Future.successful(Redirect(appConfig.acmExternalUrl))
      }
