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
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Cancelled, Expired, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConsentInformationPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.PageNotFound
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConsentInformationController @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                             serviceConfigurationService: ClientServiceConfigurationService,
                                             consentInformationPage: ConsentInformationPage,
                                             pageNotFound: PageNotFound,
                                             mcc: MessagesControllerComponents,
                                             actions: Actions,
                                             clientJourneyService: ClientJourneyService
                                            )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show(uid: String, taxService: String): Action[AnyContent] = actions.getClientJourney(taxService).async:
    implicit journeyRequest =>
      given Request[?] = journeyRequest.request

      if serviceConfigurationService.validateUrlPart(taxService) then agentClientRelationshipsConnector
        .validateInvitation(uid, serviceConfigurationService.getServiceKeysForUrlPart(taxService))
        .flatMap {
          case Left("AGENT_SUSPENDED") => Future.successful(Redirect(routes.ClientExitController.showUnauthorised(AgentSuspended)))
          case Left("INVITATION_OR_AGENT_RECORD_NOT_FOUND") => Future.successful(Redirect(routes.ClientExitController.showUnauthorised(NoOutstandingRequests)))
          case Right(response) =>
            val newJourney = journeyRequest.journey.copy(
              invitationId = Some(response.invitationId),
              serviceKey = Some(response.serviceKey),
              agentName = Some(response.agentName),
              status = Some(response.status),
              lastModifiedDate = Some(response.lastModifiedDate),
              existingMainAgent = response.existingMainAgent,
              clientType = response.clientType
            )
            clientJourneyService.saveJourney(newJourney).map(_ => response.status match {
              case Pending => Ok(consentInformationPage(newJourney))
              case Expired => Redirect(routes.ClientExitController.showClient(AuthorisationRequestExpired))
              case Cancelled => Redirect(routes.ClientExitController.showClient(AuthorisationRequestCancelled))
              case _ => Redirect(routes.ClientExitController.showClient(AlreadyRespondedToAuthorisationRequest))
            })
        }
      else Future.successful(NotFound(pageNotFound()))