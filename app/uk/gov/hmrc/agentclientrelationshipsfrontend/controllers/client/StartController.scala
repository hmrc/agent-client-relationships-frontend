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
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.ValidateLinkPartsResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.AuthoriseAgentStartPage
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StartController @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                serviceConfigurationService: ClientServiceConfigurationService,
                                authoriseAgentStartPage: AuthoriseAgentStartPage,
                                mcc: MessagesControllerComponents
                               )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

 def show(uid: String, normalizedAgentName: String, taxService: String): Action[AnyContent] = Action.async:

  implicit request =>
     given HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

     if serviceConfigurationService.validateUrlPart(taxService) then
       agentClientRelationshipsConnector
         .validateLinkParts(uid, normalizedAgentName)
         .map {
           case Left("AGENT_NOT_FOUND") => Redirect("routes.ClientExitController.show(AGENT_NOT_FOUND)")
           case Left("AGENT_SUSPENDED") => Redirect("routes.ClientExitController.show(AGENT_SUSPENDED)")
           case Left(_) => Redirect("routes.ClientExitController.show(SERVER_ERROR)")
           case Right(response) => Ok(authoriseAgentStartPage(response.name, taxService, uid))
         }
     else Future.successful(NotFound(s"TODO: NOT FOUND urlPart $taxService for Client controller/template"))