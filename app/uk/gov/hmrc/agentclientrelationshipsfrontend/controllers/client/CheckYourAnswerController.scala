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

import com.google.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Rejected, Accepted as ClientAccepted}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.CheckYourAnswerPage

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswerController @Inject()(mcc: MessagesControllerComponents,
                                          actions: Actions,
                                          agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                          checkYourAnswerPage: CheckYourAnswerPage,
                                          journeyService: ClientJourneyService
                                         )
                                         (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show: Action[AnyContent] = actions.clientAuthenticate:
    implicit request =>
      if request.journey.consent.isDefined then Ok(checkYourAnswerPage())
      else Redirect(appConfig.acmExternalUrl)

  def submit: Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      val consentAnswer: Option[Boolean] = request.journey.consent
      val invitationId: Option[String] = request.journey.invitationId

      (consentAnswer, invitationId) match {
        case (Some(true), Some(invId: String)) => for {
            _ <- agentClientRelationshipsConnector.acceptAuthorisation(invId)
            _ <- journeyService.saveJourney(ClientJourney(
              journeyType = request.journey.journeyType,
              journeyComplete = Some(invId)
            ))
          } yield Redirect(routes.ConfirmationController.show)
        case (Some(false), Some(invId)) => for {
            _ <- agentClientRelationshipsConnector.rejectAuthorisation(invId)
            _ <- journeyService.saveJourney(ClientJourney(
              journeyType = request.journey.journeyType,
              journeyComplete = Some(invId)
            ))
          } yield Redirect(routes.ConfirmationController.show)
        case _ =>
          Future.successful(Redirect(appConfig.acmExternalUrl))
      }
