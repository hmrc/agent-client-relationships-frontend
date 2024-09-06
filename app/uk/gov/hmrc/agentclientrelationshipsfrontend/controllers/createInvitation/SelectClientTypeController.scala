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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientTypeFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.select_client_type
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectClientTypeController @Inject()(mcc: MessagesControllerComponents,
                                           view: select_client_type,
                                           appConfig: AppConfig,
                                           createInvitationService: CreateInvitationService,
                                           serviceConfig: ClientServiceConfigurationService
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):

  private def previousPageUrl(cya: Boolean = false): String = if (cya) routes.CheckYourAnswersController.show.url else appConfig.agentServicesAccountHomeUrl
  private lazy val nextPageUrl: String = routes.SelectClientServiceController.show.url
  private def formSubmitUrl(cya: Boolean = false): Call = routes.SelectClientTypeController.onSubmit(cya)
  
  def show(cya: Boolean = false): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      
      createInvitationService.getAnswerFromSession(ClientTypeFieldName).map { clientTypeValue =>
        Ok(view(SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.allClientTypes).fill(clientTypeValue), serviceConfig.allClientTypes, previousPageUrl(cya), formSubmitUrl(cya)))
      }
      

  def onSubmit(cya: Boolean = false): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      
      SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.allClientTypes).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(Ok(view(formWithErrors, serviceConfig.allClientTypes, previousPageUrl(cya), formSubmitUrl(cya))))
        },
        clientType => {
          createInvitationService.deleteAllAnswersInSession.flatMap { _ =>
            createInvitationService.saveAnswerInSession(ClientTypeFieldName, clientType).map { _ =>
              Redirect(nextPageUrl)
            }
          }
        }
      )
