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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.test

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{AgentTypeFieldName, ClientNameFieldName, MainAgentType, SupportingAgentType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.select_agent_type
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

//TODO WG - we do not need that COntroller. For now it is used to start journey
@Singleton
class StartInvitationController @Inject()(mcc: MessagesControllerComponents,
                                          journeyService: JourneyService
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) {

  def startCreateInvitationJourney(): Action[AnyContent] = Action.async { implicit request =>
    val journey = journeyService.newJourney(
      journeyType = JourneyType.CreateInvitation
    )
    journeyService.saveJourney(journey)
//      .map(_ => Redirect(routes.SelectClientTypeController.show()))
      .map(_ => Redirect(routes.SelectClientTypeController.show()))
  }
}


