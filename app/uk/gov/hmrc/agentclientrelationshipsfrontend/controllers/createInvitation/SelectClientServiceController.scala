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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{ClientServiceFieldName, ClientTypeFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.select_client_service
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectClientServiceController @Inject()(mcc: MessagesControllerComponents,
                                              view: select_client_service,
                                              appConfig: AppConfig,
                                              createInvitationService: CreateInvitationService,
                                              clientServiceConfig: ClientServiceConfigurationService
                                             )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):

  private lazy val previousPageUrl: String = routes.SelectClientTypeController.show().url
  
  private def nextPageUrl(clientService: String): String =
    routes.EnterClientDetailsController.show(clientServiceConfig.firstClientDetailsFieldFor(clientService).name).url
  
  def show: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientTypeFieldName).flatMap { clientType =>
          val clientServices = clientServiceConfig.clientServicesFor(clientType)
          
          createInvitationService.getAnswerFromSession(ClientServiceFieldName).map { clientService =>
            Ok(view(SelectFromOptionsForm.form(ClientServiceFieldName, clientServices).fill(clientService), previousPageUrl, clientType, clientServices))
          }
      }

  def onSubmit: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientTypeFieldName).flatMap { clientType =>
          val clientServices = clientServiceConfig.clientServicesFor(clientType)
          
          SelectFromOptionsForm.form(ClientServiceFieldName, clientServices).bindFromRequest().fold(
            formWithErrors => {
              Future.successful(Ok(view(formWithErrors, previousPageUrl, clientType, clientServices)))
            },
            clientService => {
              createInvitationService.saveAnswerInSession(ClientServiceFieldName, clientService).map { _ =>
                  Redirect(nextPageUrl(clientService))
              }
            }
          )
      }
