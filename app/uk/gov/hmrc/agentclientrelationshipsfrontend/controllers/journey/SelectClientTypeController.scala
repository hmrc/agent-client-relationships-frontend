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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientTypeFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectClientTypePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectClientTypeController @Inject()(mcc: MessagesControllerComponents,
                                           serviceConfig: ClientServiceConfigurationService,
                                           journeyService: JourneyService,
                                           selectClientTypePage: SelectClientTypePage,
                                           actions: Actions
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:
  
  def show(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      Ok(selectClientTypePage(
        form = SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.orderedClientTypes, journey.journeyType.toString).fill(journey.getClientTypeWithDefault),
        clientTypes = serviceConfig.orderedClientTypes
      ))
      

  def onSubmit(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey

      SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.orderedClientTypes, journey.journeyType.toString).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(selectClientTypePage(
            form = formWithErrors,
            clientTypes = serviceConfig.orderedClientTypes
          )))
        },
        clientType => {
          // unset any previous answers if client type is changed
          val newJourney = if (journey.clientType.contains(clientType)) journey else journey.copy(
            clientType = Some(clientType),
            clientService = None,
            clientId = None,
            clientDetailsResponse = None,
            clientConfirmed = None,
            knownFact = None,
            agentType = None,
            confirmationClientName = None,
            journeyComplete = None
          )
          journeyService.saveJourney(newJourney).flatMap { _ =>
            // we always want to go to the services page after selecting client type
            Future.successful(Redirect(routes.SelectServiceController.show(journey.journeyType).url))
          }
        }
      )
