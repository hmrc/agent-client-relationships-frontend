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
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ClientTypeFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.createInvitation.SelectFromOptionsForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.select_client_type
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.Journey

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SelectClientTypeController @Inject()(mcc: MessagesControllerComponents,
                                           view: select_client_type,
                                           appConfig: AppConfig,
                                           journeyService: JourneyService,
                                           serviceConfig: ClientServiceConfigurationService,
                                           actions:        Actions
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:
  
  private def formSubmitUrl(cya: Boolean = false): Call = routes.SelectClientTypeController.onSubmit(cya)
  
  def show(cya: Boolean = false): Action[AnyContent] = actions.getJourney.async: 
    journeyRequest =>
      given Request[?] = journeyRequest.request
      
      val journey = journeyRequest.journey
      lazy val previousPageUrl: String = if (cya) routes.CheckYourAnswersController.show.url else appConfig.agentServicesAccountHomeUrl
      
      Future.successful(Ok(view(SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.allClientTypes).fill(journey.clientType.getOrElse("")), serviceConfig.allClientTypes, previousPageUrl, formSubmitUrl(cya))))

  
  def onSubmit(cya: Boolean = false): Action[AnyContent] = actions.getJourney.async:
    journeyRequest =>
      given Request[?] = journeyRequest.request
      val journey = journeyRequest.journey
      
      lazy val previousPageUrl: String = journey.journeyType match
        case JourneyType.CreateInvitation | JourneyType.CreateInvitationFastTrack  => if (cya) routes.CheckYourAnswersController.show.url else appConfig.agentServicesAccountHomeUrl
            
      
      SelectFromOptionsForm.form(ClientTypeFieldName, serviceConfig.allClientTypes).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(Ok(view(formWithErrors, serviceConfig.allClientTypes, previousPageUrl, formSubmitUrl(cya))))
        },
        clientType => {
            journeyService.saveJourney(journey.copy(clientType = Some(clientType))).flatMap { _ =>
              journeyService.nextPageUrl(serviceConfig).map(Redirect(_))
          }
        }
      )

//  private def nextPageUrlFastTrack(journey: Journey): String =
//    val clientService = journey.getService
//    val knowFactsType = serviceConfig.clientDetailsFor(clientService).last.name
//    val isKowFactDefined = journey.clientDetails.contains(knowFactsType)
//
//    if (isKowFactDefined) routes.CheckYourAnswersController.show.url
//    else
//      val knowFactsType = serviceConfig.clientDetailsFor(journey.getService).last.name
//      routes.EnterClientDetailsController.show(knowFactsType).url
//
//
