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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientServiceFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, AgentJourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.SelectClientServicePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectServiceController @Inject()(mcc: MessagesControllerComponents,
                                        serviceConfig: ClientServiceConfigurationService,
                                        journeyService: AgentJourneyService,
                                        selectClientServicePage: SelectClientServicePage,
                                        actions: Actions
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:
  
  def show(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      if journey.getClientTypeWithDefault.isEmpty then Redirect(routes.SelectClientTypeController.show(journey.journeyType))
      else {
        val services = serviceConfig.clientServicesFor(journey.getClientType)
        Ok(selectClientServicePage(
          form = SelectFromOptionsForm.form(ClientServiceFieldName, services, journey.journeyType.toString).fill(serviceConfig.getServiceForForm(journey.getServiceWithDefault)),
          clientType = journey.getClientType,
          services
        ))
      }
      

  def onSubmit(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      val services = serviceConfig.clientServicesFor(journey.getClientType)
      SelectFromOptionsForm.form(ClientServiceFieldName, services, journeyType.toString).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(selectClientServicePage(
            form = formWithErrors,
            clientType = journey.getClientType,
            clientServices = services
          )))
        },
        clientService => {
          // unset any previous answers if service is changed 
          val newJourney = journey.copy(
            clientService = Some(clientService),
            clientId = None,
            clientDetailsResponse = None,
            clientConfirmed = None,
            knownFact = None,
            agentType = None,
            refinedService = None,
            confirmationClientName = None,
            journeyComplete = None
          )
          
          journeyService.saveJourney(newJourney).flatMap { _ =>
            journeyService.nextPageUrl(journeyType).map(Redirect(_))}
        }
  )
