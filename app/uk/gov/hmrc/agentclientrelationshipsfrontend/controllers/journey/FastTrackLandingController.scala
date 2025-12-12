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
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.FastTrackLandingPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FastTrackLandingController @Inject()(mcc: MessagesControllerComponents,
                                           serviceConfig: ClientServiceConfigurationService,
                                           journeyService: AgentJourneyService,
                                           fasttrackLandingPage: FastTrackLandingPage,
                                           actions: Actions
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  def show(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      if journey.clientDetailsResponse.isEmpty || journey.clientId.isEmpty then Future.successful(Redirect(routes.SelectClientTypeController.show(journeyType)))
      else
        val continueUrl = journeyService.nextPageUrl(journeyType)
        continueUrl.map(url => {
          Ok(fasttrackLandingPage(continueUrl = url, clientDetailField = serviceConfig.firstClientDetailsFieldFor(journey.getService)))
        })
      

  def onSubmit(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val nextPageUrl = journeyService.nextPageUrl(journeyType)
      nextPageUrl.map(Redirect(_))
