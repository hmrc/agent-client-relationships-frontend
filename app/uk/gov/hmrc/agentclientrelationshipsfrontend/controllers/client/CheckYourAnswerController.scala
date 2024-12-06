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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.CheckYourAnswerPage

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswerController @Inject()(mcc: MessagesControllerComponents,
                                          actions: Actions,
                                          agentClientRelationshipsConnector: AgentClientRelationshipsConnector,
                                          checkYourAnswerPage: CheckYourAnswerPage
                                         )
                                         (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show: Action[AnyContent] = actions.clientAuthenticate:
    implicit request =>
      if (request.journey.consent.isDefined) Ok(checkYourAnswerPage())
      else BadRequest // TODO implement tailored page which gives some guidance to user

  def submit: Action[AnyContent] = actions.clientAuthenticate.async:
    implicit request =>
      val consentAnswer = request.journey.consent
      val invitationId = request.journey.invitationId
      (consentAnswer, invitationId) match {
        case (Some(true), Some(invId)) =>
          agentClientRelationshipsConnector.acceptAuthorisation(invId).map: _ =>
            Redirect("routes.controllers.client.ConfirmationController.show") // TODO add controller reverse routing
        case (Some(false), Some(invId)) =>
          agentClientRelationshipsConnector.rejectAuthorisation(invId).map: _ =>
            Redirect("routes.controllers.client.ConfirmationController.show") // TODO add controller reverse routing
        case _ =>
          Future.successful(BadRequest) // TODO implement tailored page which gives some guidance to user
      }
