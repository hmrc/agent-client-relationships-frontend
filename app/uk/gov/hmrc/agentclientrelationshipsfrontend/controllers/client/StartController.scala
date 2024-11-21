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
import play.api.mvc.{Action, AnyContent, Result}
import play.mvc._
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.ValidateLinkResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StartController @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                serviceConfigurationService: ClientServiceConfigurationService
                                            )(implicit executionContext: ExecutionContext,
                                              appConfig: AppConfig){

 def show(uid: String, normalizedAgentName: String, taxService: String): Action[AnyContent] = Action.async { _ =>
   if serviceConfigurationService.validateUrlPart(taxService) then 
     agentClientRelationshipsConnector
       .validateLinkParts(uid, normalizedAgentName)
       .map {
         case Left("AGENT_NOT_FOUND") => Redirect("routes.ClientErrorController.show(AGENT_NOT_FOUND)")
         case Left("AGENT_SUSPENDED") => Redirect("routes.ClientErrorController.show(AGENT_SUSPENDED)") 
         case Left("SERVER_ERROR") => Redirect("routes.ClientErrorController.show(SERVER_ERROR)")
         case Right(_) => Rediirect("ok")
       }
   else Future.successful(NotFound(""))
 }

}
