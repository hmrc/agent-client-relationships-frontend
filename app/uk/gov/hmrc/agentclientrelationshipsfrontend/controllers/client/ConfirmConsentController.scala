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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.ConfirmConsentForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ConfirmConsentPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class ConfirmConsentController @Inject()(mcc: MessagesControllerComponents,
                                         actions: Actions,
                                         confirmConsentView: ConfirmConsentPage,
                                         clientServiceConfig: ClientServiceConfigurationService,
                                         journeyService: ClientJourneyService)
                                        (implicit ec: ExecutionContext,
                                         appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  private def determineAgentRole(service: String) = clientServiceConfig.supportsAgentRoles(service) match {
    case true if clientServiceConfig.supportingAgentServices.contains(service) => "suppAgent"
    case true => "mainAgent"
    case false => "agent"
  }

  def show: Action[AnyContent] = actions.clientJourneyRequired:
    implicit request =>
      (request.journey.serviceKey, request.journey.agentName) match {
        case (Some(service), Some(agentName)) =>
          val agentRole = determineAgentRole(service)
          val form = if request.journey.consent.isDefined
            then ConfirmConsentForm.form(agentName, agentRole, service).fill(request.journey.consent.get)
            else ConfirmConsentForm.form(agentName, agentRole, service)
          Ok(confirmConsentView(form, agentRole))
        case _ => Redirect(routes.ManageYourTaxAgentsController.show.url)
      }

  def submit: Action[AnyContent] = actions.clientJourneyRequired.async:
    implicit request =>
      (request.journey.serviceKey, request.journey.agentName) match {
        case (Some(service), Some(agentName)) =>
          val agentRole = determineAgentRole(service)
          val form = ConfirmConsentForm.form(agentName, agentRole, service)
          form.bindFromRequest().fold(
            formWithErrors => {
              Future.successful(BadRequest(confirmConsentView(formWithErrors, agentRole)))
            },
            answer => {
              journeyService.saveJourney(request.journey.copy(consent = Some(answer))).map { _ =>
                Redirect(routes.CheckYourAnswerController.show)
              }
            }
          )
        case _ => Future.successful(Redirect(routes.ManageYourTaxAgentsController.show.url))
      }