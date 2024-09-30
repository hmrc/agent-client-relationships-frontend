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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{ClientNameFieldName, ClientServiceFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.EnterClientDetailsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, CreateInvitationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.enter_client_details
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterClientDetailsController @Inject()(mcc: MessagesControllerComponents,
                                             view: enter_client_details,
                                             clientServiceConfig: ClientServiceConfigurationService,
                                             createInvitationService: CreateInvitationService,
                                             agentClientRelationshipsConnector: AgentClientRelationshipsConnector
                                            )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):
  
  private def previousPageUrl(clientService: String, currentFieldName: String): String = {
    val firstFieldName = clientServiceConfig.firstClientDetailsFieldFor(clientService).name

    if (currentFieldName.equalsIgnoreCase(firstFieldName)) { // this is field 1/1 or 1/2
      routes.SelectClientServiceController.show.url
    } else { // this is field 2/2
      routes.EnterClientDetailsController.show(firstFieldName).url
    }
  }
  
  private lazy val nextPageUrl: String = routes.ConfirmClientController.show.url

  def show(fieldName: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientServiceFieldName).flatMap { clientService =>
        val clientDetailsFieldConfiguration = clientServiceConfig.fieldConfigurationFor(clientService, fieldName)

        createInvitationService.getAnswerFromSession(fieldName).map { fieldAnswer =>
          val form = if (fieldAnswer.nonEmpty) {
            EnterClientDetailsForm.form(clientDetailsFieldConfiguration).fill(fieldAnswer)
          } else {
            EnterClientDetailsForm.form(clientDetailsFieldConfiguration)
          }

          Ok(view(form, clientDetailsFieldConfiguration, previousPageUrl(clientService, fieldName)))
        }
      }

  def onSubmit(fieldName: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      createInvitationService.getAnswerFromSession(ClientServiceFieldName).flatMap { clientService =>
        val clientDetailsFieldConfiguration = clientServiceConfig.fieldConfigurationFor(clientService, fieldName)

        EnterClientDetailsForm.form(clientDetailsFieldConfiguration).bindFromRequest().fold(
          formWithErrors => {
            Future.successful(Ok(view(formWithErrors, clientDetailsFieldConfiguration, previousPageUrl(clientService, fieldName))))
          },
          clientDetailsData => {
            createInvitationService.saveAnswerInSession(fieldName, clientDetailsData).flatMap { _ =>
              val lastFieldName = clientServiceConfig.clientDetailsFor(clientService).last.name

              if (fieldName.equalsIgnoreCase(lastFieldName)) { // this is field 1/1 or 2/2
                agentClientRelationshipsConnector.getClientDetails.flatMap { clientName =>
                  createInvitationService.saveAnswerInSession(ClientNameFieldName, clientName).map { _ =>
                    Redirect(nextPageUrl)
                  }
                }
              } else { // this is field 1/2
                Future.successful(Redirect(routes.EnterClientDetailsController.show(lastFieldName).url))
              }
            }
          }
        )
      }
