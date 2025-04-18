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

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.{AgentNotFoundError, AgentSuspendedError}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.AuthoriseAgentStartPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.PageNotFound
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StartController @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                serviceConfigurationService: ClientServiceConfigurationService,
                                authoriseAgentStartPage: AuthoriseAgentStartPage,
                                pageNotFound: PageNotFound,
                                mcc: MessagesControllerComponents
                               )(implicit val executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  // TEST-ONLY NOTE: unauthenticated access to this route may not work when using stubs without an existing agent cache in agent-assurance
  // bypass issue by either accessing as authenticated client or ensure cache for agent record exists first before signing out
  def show(uid: String, normalizedAgentName: String, taxService: String): Action[AnyContent] = Action.async:
    implicit request =>
      if serviceConfigurationService.validateUrlPart(taxService) then
        agentClientRelationshipsConnector
          .validateLinkParts(uid, normalizedAgentName)
          .map {
            case Left(AgentSuspendedError) => Redirect(routes.ClientExitController.showExit(AgentSuspended, None, taxService))
            case Left(AgentNotFoundError) => Redirect(routes.ClientExitController.showExit(NoOutstandingRequests, None, taxService))
            case Right(response) => Ok(authoriseAgentStartPage(response.name, taxService, uid))
          }
      else Future.successful(NotFound(pageNotFound()))