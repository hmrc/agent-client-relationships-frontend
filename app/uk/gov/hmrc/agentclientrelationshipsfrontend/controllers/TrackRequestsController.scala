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
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, TrackRequestsService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.TrackRequestsPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
@Singleton
class TrackRequestsController @Inject()(
                                         mcc: MessagesControllerComponents,
                                         actions: Actions,
                                         trackRequestsService: TrackRequestsService,
                                         acrService: AgentClientRelationshipsService,
                                         trackRequestsPage: TrackRequestsPage
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

   def deAuthFromInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate:
     request =>
       given AgentRequest[?] = request
       // we can fetch the invitation, validate the arn and populate a new AgentJourney of type `AgentCancelAuthorisation`
       // then redirect to nextUrl in that journey
       Ok("de-authorise relationship called with invitation id " + invitationId)

  def restartInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate:
    request =>
      given AgentRequest[?] = request
      // we can fetch the invitation, validate the arn and populate a new AgentJourney of type `AuthorisationRequest`
      // then redirect to nextUrl in that journey
      Ok("restart invitation called with invitation id " + invitationId)

  def resendInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate:
    request =>
      given AgentRequest[?] = request
      // we can fetch the invitation info, validate the arn and feed it into a new template to display the link to resend the invitation
      Ok("resend invitation called with invitation id " + invitationId)

  def cancelInvitation(invitationId: String): Action[AnyContent] = actions.agentAuthenticate:
    request =>
      given AgentRequest[?] = request
      // we can fetch the invitation, validate the arn and populate a new AgentJourney of type `CancelInvitation`
      // which does not yet exist - so that journey type needs to be created
      Ok("cancel invitation called with invitation id " + invitationId)
}
