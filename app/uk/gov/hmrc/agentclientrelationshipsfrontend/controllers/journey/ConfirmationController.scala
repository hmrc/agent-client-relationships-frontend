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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AgentCancelAuthorisationResponse, AuthorisationRequestInfo}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.{AgentCancelAuthorisationCompletePage, CreateAuthorisationRequestCompletePage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmationController @Inject()(mcc: MessagesControllerComponents,
                                       agentClientRelationshipsService: AgentClientRelationshipsService,
                                       createAuthorisationRequestCompletePage: CreateAuthorisationRequestCompletePage,
                                       agentCancelAuthorisationCompletePage: AgentCancelAuthorisationCompletePage,
                                       serviceConfig: ClientServiceConfigurationService,
                                       actions: Actions
                                       )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  private def makeClientLink(authorisationRequestInfo: AuthorisationRequestInfo): String =
    s"${appConfig.baseUrl}/agent-client-relationships/appoint-someone-to-deal-with-HMRC-for-you/${authorisationRequestInfo.agentReference}/${authorisationRequestInfo.normalizedAgentName}/${serviceConfig.getUrlPart(authorisationRequestInfo.service)}"

  def show(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      if journeyRequest.journey.journeyComplete.isEmpty then Future.successful(Redirect(appConfig.agentServicesAccountHomeUrl))
      else
        val journeyCompleteString = journeyRequest.journey.journeyComplete.get
        journeyType match
          case AgentJourneyType.AuthorisationRequest =>
            agentClientRelationshipsService.getAuthorisationRequest(invitationId = journeyCompleteString).map {
              case Some(authorisationRequestInfo) =>
                Ok(createAuthorisationRequestCompletePage(authorisationRequestInfo, makeClientLink(authorisationRequestInfo)))
              case None =>
                throw new RuntimeException(s"Authorisation request not found for invitationId: $journeyCompleteString")
            }
          case AgentJourneyType.AgentCancelAuthorisation =>
            agentClientRelationshipsService.getAgentDetails().map {
              case Some(agentDetails) => Ok(agentCancelAuthorisationCompletePage(AgentCancelAuthorisationResponse(
                clientName = journeyRequest.journey.confirmationClientName.getOrElse(""),
                agentName = agentDetails.agencyName,
                service = journeyRequest.journey.confirmationService.getOrElse(""),
                date = journeyCompleteString
              )))
              case _ => throw new RuntimeException("Agent details not found")
            }
