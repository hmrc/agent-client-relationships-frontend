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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.createInvitation

import actions.Actions
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{ClientServiceFieldName, ClientTypeFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.select_client_service
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.requests.JourneyRequest
import play.api.i18n.I18nSupport
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectClientServiceController @Inject()(mcc: MessagesControllerComponents,
                                              view: select_client_service,
                                              appConfig: AppConfig,
                                              journeyService: JourneyService,
                                              clientServiceConfig: ClientServiceConfigurationService,
                                              gentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                              actions:        Actions
                                             )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  private lazy val previousPageUrl: String = routes.SelectClientTypeController.show().url
  
  private def nextPageUrl(clientService: String): String =
    routes.EnterClientDetailsController.show(clientServiceConfig.firstClientDetailsFieldFor(clientService).name).url
  
  def show: Action[AnyContent] = actions.getJourney.async:
    journeyRequest =>
      given Request[?] = journeyRequest.request

      //Raise error when clientType not defined
      val clientType = journeyRequest.journey.getClientType
      val clientServices = clientServiceConfig.clientServicesFor(clientType)
      Future.successful(Ok(view(SelectFromOptionsForm.form(ClientServiceFieldName, clientServices).fill(journeyRequest.journey.service.getOrElse("")), previousPageUrl, clientType, clientServices)))



  def onSubmit: Action[AnyContent] = actions.getJourney.async:
    journeyRequest =>
      given Request[?] = journeyRequest.request
      
      val clientType = journeyRequest.journey.clientType.getOrElse("")
      val clientServices = clientServiceConfig.clientServicesFor(clientType)
      SelectFromOptionsForm.form(ClientServiceFieldName, clientServices).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(Ok(view(formWithErrors, previousPageUrl, clientType, clientServices)))
        },
        clientService => {

          journeyService.saveJourney(journeyRequest.journey.copy(service = Some(clientService))).flatMap { _ =>
            journeyService.nextPageUrl(clientServiceConfig).map(Redirect(_))
          }
        }
      )
