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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest, Request}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.{ClientNameFieldName, ClientServiceFieldName}
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.EnterClientDetailsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.enter_client_details
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.requests.JourneyRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterClientDetailsController @Inject()(mcc: MessagesControllerComponents,
                                             view: enter_client_details,
                                             clientServiceConfig: ClientServiceConfigurationService,
                                             journeyService: JourneyService,
                                             agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                             actions:        Actions
                                            )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:
  
  private def previousPageUrl(clientService: String, currentFieldName: String): String = {
    val firstFieldName = clientServiceConfig.firstClientDetailsFieldFor(clientService).name

    if (currentFieldName.equalsIgnoreCase(firstFieldName)) { // this is field 1/1 or 1/2
      routes.SelectClientServiceController.show.url
    } else { // this is field 2/2
      routes.EnterClientDetailsController.show(firstFieldName).url
    }
  }
  
  private lazy val nextPageUrl: String = routes.ConfirmClientController.show.url

  def show(fieldName: String): Action[AnyContent] = actions.getJourney.async:
    
    journeyRequest =>
      given Request[?] = journeyRequest.request

      println(s"${Console.MAGENTA} Wojciech show clientDetails for $fieldName ${Console.RESET}")
      
      val clientService = journeyRequest.journey.service.getOrElse("")
      val clientDetailsFieldConfiguration = clientServiceConfig.fieldConfigurationFor(clientService, fieldName)
      val form = journeyRequest.journey.clientDetails.get(fieldName) match
        case Some(fieldAnswer) => EnterClientDetailsForm.form(clientDetailsFieldConfiguration).fill(fieldAnswer)
        case None => EnterClientDetailsForm.form(clientDetailsFieldConfiguration)
      Future.successful(Ok(view(form, clientDetailsFieldConfiguration, previousPageUrl(clientService, fieldName))))
      


  def onSubmit(fieldName: String): Action[AnyContent] = actions.getJourney.async:
    journeyRequest =>
      given Request[?] = journeyRequest.request

      val journey = journeyRequest.journey
      val clientService = journeyRequest.journey.service.getOrElse("")
      val clientDetailsFieldConfiguration = clientServiceConfig.fieldConfigurationFor(clientService, fieldName)

      EnterClientDetailsForm.form(clientDetailsFieldConfiguration).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(Ok(view(formWithErrors, clientDetailsFieldConfiguration, previousPageUrl(clientService, fieldName))))
        },
        clientDetailsData => {
          println(s"${Console.MAGENTA} Wojciech clientDetails before $fieldName: ${journey.clientDetails} ${Console.RESET}")
          val c = journey.clientDetails + (fieldName -> clientDetailsData)
          println(s"${Console.MAGENTA} Wojciech clientDetails should be  $c ${Console.RESET}")
          val updatedJourney = journey.copy(clientDetails = journey.clientDetails + (fieldName -> clientDetailsData))
          println(s"${Console.MAGENTA} Wojciech clientDetails after $fieldName: ${updatedJourney.clientDetails} ${Console.RESET}")
          
          journeyService.saveJourney(updatedJourney).flatMap { _ =>
            val lastFieldName = clientServiceConfig.clientDetailsFor(clientService).last.name

            if (fieldName.equalsIgnoreCase(lastFieldName)) { // this is field 1/1 or 2/2
              println(s"${Console.MAGENTA} Wojciech going to nextPageUrl:$nextPageUrl ${Console.RESET}")
              agentClientRelationshipsConnector.getClientDetails.flatMap { clientName =>
                journeyService.saveJourney(updatedJourney.copy(clientName = Some(clientName))).flatMap { _ =>
                  journeyService.nextPageUrl(clientServiceConfig).map(Redirect(_))
                }
              }
            } else { // this is field 1/2
              println(s"${Console.MAGENTA} Wojciech going to EnterClientDetailsController for lastFieldName:$lastFieldName ${Console.RESET}")
              Future.successful(Redirect(routes.EnterClientDetailsController.show(lastFieldName).url))
            }
          }
        }
      )
      
