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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation

import actions.Actions
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest, Request}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.check_your_answers
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.requests.JourneyRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(mcc: MessagesControllerComponents,
                                           view: check_your_answers,
                                           clientServiceConfig: ClientServiceConfigurationService,
                                           journeyService: JourneyService,
                                           agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                           actions:        Actions
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  private lazy val previousPageUrl: String = routes.SelectAgentTypeController.show.url

  private def nextPageUrl(invitationId: String): String = routes.InvitationCreatedController.show(invitationId).url


  def show: Action[AnyContent] = actions.getJourney.async:
    journeyRequest =>
      given Request[?] = journeyRequest.request

      val journey = journeyRequest.journey
      val clientName = journeyRequest.journey.clientName.getOrElse("")
      val clientService = journeyRequest.journey.service.getOrElse("")
      val clientType = journeyRequest.journey.clientType.getOrElse("")
      val agentType = journeyRequest.journey.agentType.getOrElse("")

      Future.successful(Ok(view(previousPageUrl, clientType = clientType, clientService = clientService, clientName = clientName, agentType = agentType)))




  def onSubmit: Action[AnyContent] = actions.getJourney.async:
    journeyRequest =>
      given Request[?] = journeyRequest.request

      agentClientRelationshipsConnector.createInvitation.flatMap { invitationId =>
        journeyService.deleteAllAnswersInSession().map { _ =>
          Redirect(nextPageUrl(invitationId))
        }
      }
