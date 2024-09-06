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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AgentTypeFieldName, ClientNameFieldName, MainAgentType, SupportingAgentType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.select_agent_type
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectAgentTypeController @Inject()(mcc: MessagesControllerComponents,
                                          view: select_agent_type,
                                          clientServiceConfig: ClientServiceConfigurationService,
                                          createInvitationService: CreateInvitationService
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):

  private lazy val previousPageUrl: String = routes.ConfirmClientController.show.url

  private lazy val nextPageUrl: String = routes.CheckYourAnswersController.show.url
  
  private val agentTypes: Set[String] = Set(MainAgentType, SupportingAgentType)
  
  def show: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      
      createInvitationService.getAnswerFromSession(ClientNameFieldName).flatMap { clientName =>
        createInvitationService.getAnswerFromSession(AgentTypeFieldName).map { agentType =>
          val form = SelectFromOptionsForm.form(AgentTypeFieldName, agentTypes).fill(agentType)
          Ok(view(form, agentTypes, previousPageUrl, clientName))
        }
      }
  

  def onSubmit: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientNameFieldName).flatMap { clientName =>
        SelectFromOptionsForm.form(AgentTypeFieldName, agentTypes).bindFromRequest().fold(
          formWithErrors => {
            Future.successful(Ok(view(formWithErrors, agentTypes, previousPageUrl, clientName)))
          },
          agentType => {
            createInvitationService.saveAnswerInSession(AgentTypeFieldName, agentType).map { _ =>
              Redirect(nextPageUrl)
            }
          }
        )
      }
