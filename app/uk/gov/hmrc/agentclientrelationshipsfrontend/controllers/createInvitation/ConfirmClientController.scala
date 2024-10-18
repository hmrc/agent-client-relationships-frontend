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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{ClientNameFieldName, ClientServiceFieldName, ConfirmationFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.ConfirmationForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.confirm_client
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmClientController @Inject()(mcc: MessagesControllerComponents,
                                        view: confirm_client,
                                        clientServiceConfig: ClientServiceConfigurationService,
                                        createInvitationService: CreateInvitationService
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):

  private def previousPageUrl(clientService: String): String =
    routes.EnterClientDetailsController.show(clientServiceConfig.lastClientDetailsFieldFor(clientService).name).url

  private lazy val nextPageUrl: String = routes.SelectAgentTypeController.show.url

  private def restartEnterClientDetailsUrl(clientService: String): String =
    routes.EnterClientDetailsController.show(clientServiceConfig.firstClientDetailsFieldFor(clientService).name).url
  
  def show: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientNameFieldName).flatMap { clientName =>
        createInvitationService.getAnswerFromSession(ClientServiceFieldName).flatMap { clientService =>
          createInvitationService.getAnswerFromSession(ConfirmationFieldName).map { clientConfirmed =>
            val form = clientConfirmed.toBooleanOption match {
              case Some(data) => ConfirmationForm.form(ConfirmationFieldName).fill(data)
              case _ => ConfirmationForm.form(ConfirmationFieldName)
            }

            Ok(view(form, previousPageUrl(clientService), clientName))
          }
        }
      }


  def onSubmit: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientServiceFieldName).flatMap { clientService =>
        ConfirmationForm.form(ConfirmationFieldName).bindFromRequest().fold(
          formWithErrors => {
            createInvitationService.getAnswerFromSession(ClientNameFieldName).map { clientName =>
              Ok(view(formWithErrors, previousPageUrl(clientService), clientName))
            }
          },
          clientConfirmed => {
            if clientConfirmed then {
              createInvitationService.saveAnswerInSession(ConfirmationFieldName, clientConfirmed.toString).map { _ =>
                Redirect(nextPageUrl)
              }
            } else {
              Future.successful(Redirect(restartEnterClientDetailsUrl(clientService)))
            }
          }
        )
      }
