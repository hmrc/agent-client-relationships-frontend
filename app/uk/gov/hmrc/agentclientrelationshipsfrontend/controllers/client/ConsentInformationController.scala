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

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConsentInformationPage
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.ClientResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConsentInformationController @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                             serviceConfigurationService: ClientServiceConfigurationService,
                                             consentInformationPage: ConsentInformationPage,
                                             mcc: MessagesControllerComponents,
                                             actions: Actions,
                                             journeyService: ClientJourneyService
                                            )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show(uid: String, taxService: String): Action[AnyContent] = actions.getClientJourney(taxService).async:
    implicit journeyRequest =>
      given Request[?] = journeyRequest.request
      
      if serviceConfigurationService.validateUrlPart(taxService) then agentClientRelationshipsConnector
          .validateInvitation(uid, serviceConfigurationService.getServiceKeysForUrlPart(taxService))
          .flatMap {
            case Left("AGENT_SUSPENDED") => Future.successful(Redirect("routes.ClientExitController.show(AGENT_SUSPENDED)"))
            case Left("INVITATION_OR_AGENT_RECORD_NOT_FOUND") => Future.successful(Redirect("routes.ClientExitController.show(INVITATION_OR_AGENT_RECORD_NOT_FOUND)"))
            case Left(_) => Future.successful(Redirect("routes.ClientExitController.show(SERVER_ERROR)"))
            case Right(response) => {
              journeyService.saveJourney(journeyRequest.journey.copy(
                invitationId = Some(response.invitationId),
                serviceKey = Some(response.serviceKey),
                agentName = Some(response.agentName),
                status = Some(response.status),
                lastModifiedDate = Some(response.lastModifiedDate)
              )).map (_ => Ok(consentInformationPage()))
            }
          }
      else Future.successful(NotFound(s"TODO: NOT FOUND urlPart ${taxService} for Client controller/template"))
