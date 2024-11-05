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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ServiceRefinementPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceRefinementController @Inject()(mcc: MessagesControllerComponents,
                                            serviceConfig: ClientServiceConfigurationService,
                                            journeyService: JourneyService,
                                            serviceRefinementPage: ServiceRefinementPage,
                                            actions: Actions
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:
  
  def show(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      if journey.getClientTypeWithDefault.isEmpty then Redirect(routes.SelectClientTypeController.show(journey.journeyType))
      else {
        val options = serviceConfig.getSupportedEnrolments(journey.getService)
        val prepop = journey.refinedService match {
          case Some(true) => journey.getService
          case _ => ""
        }
        Ok(serviceRefinementPage(
          form = SelectFromOptionsForm.form(ClientServiceFieldName, options, journey.journeyType.toString).fill(prepop),
          options
        ))
      }
      

  def onSubmit(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest
      val journey = journeyRequest.journey
      val options = serviceConfig.getSupportedEnrolments(journey.getServiceWithDefault)
      SelectFromOptionsForm.form(ClientServiceFieldName, options, journeyType.toString).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(serviceRefinementPage(
            formWithErrors,
            options
          )))
        },
        clientService => {
          // unset any previous answers if service is changed 
          val newJourney = journey.copy(
            clientService = Some(clientService),
            clientId = None,
            clientDetailsResponse = None,
            clientConfirmed = false,
            agentType = None,
            refinedService = Some(true)
          )
          
          journeyService.saveJourney(newJourney).flatMap { _ =>
            journeyService.nextPageUrl(journeyType).map(Redirect(_))}
        }
  )
