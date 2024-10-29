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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.{Actions, AgentRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.AuthorisationRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.journey.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StartJourneyController @Inject()(mcc: MessagesControllerComponents,
                                       journeyService: JourneyService,
                                       actions:        Actions
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:
  
  
  def startJourney(journeyType: JourneyType): Action[AnyContent] = actions.authenticate.async:
    request =>
      given AgentRequest[?] = request
      
      val newJourney = journeyService.newJourney(journeyType)

      journeyService.saveJourney(newJourney).flatMap { _ =>
        Future.successful(Redirect(routes.SelectClientTypeController.show(journeyType)))
      }
