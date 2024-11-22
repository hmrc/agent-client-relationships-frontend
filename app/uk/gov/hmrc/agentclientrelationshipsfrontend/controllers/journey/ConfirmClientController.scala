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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientConfirmationFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.ConfirmClientForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentClientRelationshipsService, ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.ConfirmClientPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmClientController @Inject()(mcc: MessagesControllerComponents,
                                        serviceConfig: ClientServiceConfigurationService,
                                        journeyService: JourneyService,
                                        confirmClientPage: ConfirmClientPage,
                                        actions: Actions
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  def show(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey
      if journey.clientDetailsResponse.isEmpty then Redirect(routes.EnterClientIdController.show(journey.journeyType))
      else {
        val clientName = journey.clientDetailsResponse.get.name
        Ok(confirmClientPage(
          form = ConfirmClientForm.form(ClientConfirmationFieldName, clientName, journey.journeyType.toString),
        ))
      }


  def onSubmit(journeyType: JourneyType): Action[AnyContent] = actions.getJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey
      val clientName = journey.clientDetailsResponse.get.name

      ConfirmClientForm.form("confirmClient", clientName, journey.journeyType.toString).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(confirmClientPage(formWithErrors)))
        },
        confirmClient => {
          journeyService.saveJourney(journey.copy(clientConfirmed = Some(confirmClient))).flatMap { _ =>
            journeyService.nextPageUrl(journeyType).map(Redirect(_))
          }
        }
      )
