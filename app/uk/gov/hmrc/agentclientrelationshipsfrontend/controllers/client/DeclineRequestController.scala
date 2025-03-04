/*
 * Copyright 2025 HM Revenue & Customs
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

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Cancelled, Expired, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.DeclineRequestForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{ClientJourney, ClientJourneyRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.DeclineRequestPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class DeclineRequestController @Inject()(mcc: MessagesControllerComponents,
                                         actions: Actions,
                                         declineRequestView: DeclineRequestPage,
                                         clientServiceConfig: ClientServiceConfigurationService,
                                         clientJourneyService: ClientJourneyService,
                                         agentClientRelationshipsConnector: AgentClientRelationshipsConnector)
                                        (implicit ec: ExecutionContext,
                                         appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  private def determineAgentRole(service: String) = clientServiceConfig.supportsAgentRoles(service) match {
    case true if clientServiceConfig.supportingAgentServices.contains(service) => "suppAgent"
    case true => "mainAgent"
    case false => "agent"
  }

  def show(uid: String, taxService: String): Action[AnyContent] = actions.getClientJourney(taxService).async:
    implicit request =>
      agentClientRelationshipsConnector
        .validateInvitation(uid, clientServiceConfig.getServiceKeysForUrlPart(taxService))
        .flatMap {
          case Left("AGENT_SUSPENDED") =>
            Future.successful(Redirect(routes.ClientExitController.showUnauthorised(AgentSuspended)))
          case Left("INVITATION_OR_AGENT_RECORD_NOT_FOUND") =>
            Future.successful(Redirect(routes.ClientExitController.showUnauthorised(NoOutstandingRequests)))
          case Right(response) =>
            val newJourney = request.journey.copy(
              invitationId = Some(response.invitationId),
              serviceKey = Some(response.serviceKey),
              agentName = Some(response.agentName),
              status = Some(response.status),
              lastModifiedDate = Some(response.lastModifiedDate),
              existingMainAgent = response.existingMainAgent,
              clientType = response.clientType
            )
            val updatedRequest = ClientJourneyRequest(newJourney, request.request)
            val agentRole = determineAgentRole(response.serviceKey)
            val form = DeclineRequestForm.form(response.agentName)
            clientJourneyService.saveJourney(newJourney).map(_ => response.status match {
              case Pending =>
                Ok(declineRequestView(form, agentRole, uid, taxService)(updatedRequest, request2Messages, appConfig))
              case Expired =>
                Redirect(routes.ClientExitController.showClient(AuthorisationRequestExpired))
              case Cancelled =>
                Redirect(routes.ClientExitController.showClient(AuthorisationRequestCancelled))
              case _ =>
                Redirect(routes.ClientExitController.showClient(AlreadyRespondedToAuthorisationRequest))
            })
        }

  def submit(uid: String, taxService: String): Action[AnyContent] = actions.getClientJourney(taxService).async:
    implicit request =>
      (request.journey.serviceKey, request.journey.agentName, request.journey.invitationId) match {
        case (Some(service), Some(agentName), Some(invId)) =>
          val form = DeclineRequestForm.form(agentName)
          form.bindFromRequest().fold(
            formWithErrors => {
              val agentRole = determineAgentRole(service)
              Future.successful(BadRequest(declineRequestView(formWithErrors, agentRole, uid, taxService)))
            },
            {
              case true =>
                for {
                  _ <- agentClientRelationshipsConnector.rejectAuthorisation(invId)
                  _ <- clientJourneyService.saveJourney(ClientJourney(
                    journeyType = request.journey.journeyType,
                    journeyComplete = Some(invId)
                  ))
                } yield Redirect(routes.ConfirmationController.show)
              case false => Future.successful(Redirect(routes.ConsentInformationController.show(uid, taxService)))
            }
          )
        case _ => Future.successful(BadRequest)
      }