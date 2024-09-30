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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.check_your_answers
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(mcc: MessagesControllerComponents,
                                           view: check_your_answers,
                                           clientServiceConfig: ClientServiceConfigurationService,
                                           createInvitationService: CreateInvitationService,
                                           agentClientRelationshipsConnector: AgentClientRelationshipsConnector
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):

  private lazy val previousPageUrl: String = routes.SelectAgentTypeController.show.url

  private def nextPageUrl(invitationId: String): String = routes.InvitationCreatedController.show(invitationId).url


  def show: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      val answersFromSession = for {
        clientType <- createInvitationService.getAnswerFromSession(ClientTypeFieldName)
        clientService <- createInvitationService.getAnswerFromSession(ClientServiceFieldName)
        clientName <- createInvitationService.getAnswerFromSession(ClientNameFieldName)
        agentType <- createInvitationService.getAnswerFromSession(AgentTypeFieldName)
      } yield (clientType, clientService, clientName, agentType)
      
      answersFromSession.map { answers =>
        Ok(view(previousPageUrl, clientType = answers._1, clientService = answers._2, clientName = answers._3, agentType = answers._4))
      }



  def onSubmit: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      agentClientRelationshipsConnector.createInvitation.flatMap { invitationId =>
        createInvitationService.deleteAllAnswersInSession().map { _ =>
          Redirect(nextPageUrl(invitationId))
        }
      }
