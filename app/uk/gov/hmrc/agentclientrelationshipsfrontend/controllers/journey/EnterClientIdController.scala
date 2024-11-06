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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientTypeFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientIdForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.EnterClientIdPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterClientIdController @Inject()(mcc: MessagesControllerComponents,
                                        serviceConfig: ClientServiceConfigurationService,
                                        journeyService: JourneyService,
                                        agentClientRelationshipsService: AgentClientRelationshipsService,
                                        enterClientIdPage: EnterClientIdPage,
                                        actions: Actions
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  def show(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey
      if journey.getServiceWithDefault.isEmpty then Redirect(routes.SelectClientTypeController.show(journey.journeyType))
      else
        Ok(enterClientIdPage(
          form = EnterClientIdForm.form(serviceConfig.firstClientDetailsFieldFor(journey.getService), journey.journeyType.toString).fill(journey.clientId.getOrElse("")),
          clientDetailField = serviceConfig.firstClientDetailsFieldFor(journey.getService)
        ))


  def onSubmit(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey
      val field = serviceConfig.firstClientDetailsFieldFor(journey.getService)

      EnterClientIdForm.form(field, journey.journeyType.toString).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(enterClientIdPage(
            formWithErrors,
            field
          )))
        },
        clientId => {
          // unset any previous answers if client id is changed
          if journey.clientId.contains(clientId) && journey.clientDetailsResponse.nonEmpty then journeyService.nextPageUrl(journeyType).map(Redirect(_))
          else
            for {
              clientDetailsResponse <- agentClientRelationshipsService.getClientDetails(clientId, journey)
              _ <- journeyService.saveJourney(journey.copy(
                clientService = if journey.getServiceWithDefault.matches("HMRC-TERS-ORG") && clientId.matches(serviceConfig.urnRegex) then Some("HMRC-TERSNT-ORG") else Some(journey.getServiceWithDefault),
                clientId = Some(clientId),
                clientDetailsResponse = clientDetailsResponse,
                clientConfirmed = false,
                agentType = None
              ))
              nextPage <- if clientDetailsResponse.nonEmpty then journeyService.nextPageUrl(journeyType) else Future.successful(routes.JourneyErrorController.show(journeyType, "client-not-found").url)
            } yield Redirect(nextPage)
        }
      )
