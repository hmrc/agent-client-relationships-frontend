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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.{Actions, AgentRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AuthorisationRequestInfo
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, AgentJourneyService, ClientServiceConfigurationService, TrackRequestsService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.PageNotFound
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.trackRequests.{TrackRequestsPage, ResendInvitationLink}

import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TrackRequestsController @Inject()(
                                         mcc: MessagesControllerComponents,
                                         actions: Actions,
                                         trackRequestsService: TrackRequestsService,
                                         acrService: AgentClientRelationshipsService,
                                         trackRequestsPage: TrackRequestsPage,
                                         serviceConfig: ClientServiceConfigurationService,
                                         resendInvitationLinkPage: ResendInvitationLink,
                                         journeyService: AgentJourneyService,
                                         pageNotFound: PageNotFound
                                       )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  def show(pageNumber: Int, statusFilter: Option[String] = None, clientName: Option[String] = None): Action[AnyContent] = actions.agentAuthenticate.async:
    request =>
      given AgentRequest[?] = request
      for{
        result <- trackRequestsService.trackRequests(request.arn, pageNumber, statusFilter, clientName)
      } yield {
        Ok(trackRequestsPage(result, pageNumber))
      }
      
   def submitFilters: Action[AnyContent] = actions.agentAuthenticate.async:
     request =>
       given AgentRequest[?] = request
        val filtersApplied: Option[Map[String, Seq[String]]] = request.request.body.asFormUrlEncoded
        val statusFilter = filtersApplied.flatMap(_.get("statusFilter")).flatMap(_.headOption)
        val clientName = filtersApplied.flatMap(_.get("clientFilter")).flatMap(_.headOption)
        for{
          result <- trackRequestsService.trackRequests(request.arn, 1, statusFilter, clientName)
        } yield {
          Ok(trackRequestsPage(result, 1))
        }

   def deAuthFromInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate.async:
     request =>
       given AgentRequest[?] = request
       val journeyType: AgentJourneyType = AgentJourneyType.AgentCancelAuthorisation
       for {
         fastTrackData <- acrService.getAuthorisationRequest(invitationId = invitationId)
           .map(_.getOrElse(throw new RuntimeException(s"Invitation not found for invitationId: $invitationId")))
         clientDetails  <- acrService.getClientDetails(fastTrackData.suppliedClientId, fastTrackData.service)

         newJourney = journeyService.newJourney(journeyType)
           .copy(
             clientType = Some(fastTrackData.clientType),
             clientService = Some(fastTrackData.service),
             refinedService = Some(true), // all invitations will have been refined
             clientId = Some(fastTrackData.suppliedClientId),
             clientDetailsResponse = clientDetails,
             knownFact = clientDetails.flatMap(_.knownFacts.headOption),
             clientConfirmed = Some(true)
           )

         _ <- journeyService.saveJourney(newJourney)
         nextPage <- journeyService.nextPageUrl(journeyType)
       } yield Redirect(nextPage)

  def restartInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate.async:
    request =>
      given AgentRequest[?] = request

      val journeyType: AgentJourneyType = AgentJourneyType.AuthorisationRequest
      for {
        fastTrackData <- acrService.getAuthorisationRequest(invitationId = invitationId)
          .map(_.getOrElse(throw new RuntimeException(s"Invitation to restart not found for invitationId: $invitationId")))
        clientDetails  <- acrService.getClientDetails(fastTrackData.suppliedClientId, fastTrackData.service)

        newJourney = journeyService.newJourney(journeyType)
          .copy(
            clientType = Some(fastTrackData.clientType),
            clientService = Some(fastTrackData.service),
            refinedService = Some(true), // all invitations will have been refined
            clientId = Some(fastTrackData.suppliedClientId),
            clientDetailsResponse = clientDetails,
            knownFact = None
          )

        _ <- journeyService.saveJourney(newJourney)
        nextPage <- journeyService.nextPageUrl(journeyType)
      } yield Redirect(nextPage)

  def resendInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate.async:
    request =>
      given AgentRequest[?] = request

      acrService.getAuthorisationRequest(invitationId = invitationId).map {
        case Some(info) =>
          Ok(resendInvitationLinkPage(info,
            s"${appConfig.clientLinkBaseUrl}/${info.agentReference}/${info.normalizedAgentName}/${serviceConfig.getUrlPart(info.service)}"
          ))
        case None => NotFound(pageNotFound())
      }
}
