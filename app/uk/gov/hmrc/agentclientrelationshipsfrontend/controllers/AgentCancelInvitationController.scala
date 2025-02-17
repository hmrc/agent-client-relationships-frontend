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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.{Actions, AgentRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.AgentCancelInvitationForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentClientRelationshipsService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.{AgentCancelInvitationCompletePage, AgentCancelInvitationPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentCancelInvitationController @Inject()(
                                         mcc: MessagesControllerComponents,
                                         actions: Actions,
                                         acrService: AgentClientRelationshipsService,
                                         agentCancelInvitationPage: AgentCancelInvitationPage,
                                         agentCancelInvitationCompletePage: AgentCancelInvitationCompletePage
                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show(invitationId: String): Action[AnyContent] = actions.agentAuthenticate.async:
    request =>
      given AgentRequest[?] = request

      acrService.getAuthorisationRequest(invitationId = invitationId).map {
        case Some(info) =>
          Ok(agentCancelInvitationPage(AgentCancelInvitationForm.form, info))
        case None =>
          throw new RuntimeException(s"Invitation not found for invitationId: $invitationId")
      }

  def submit(invitationId: String): Action[AnyContent] = actions.agentAuthenticate.async:
    request =>
      given AgentRequest[?] = request
      AgentCancelInvitationForm.form.bindFromRequest().fold(
        formWithErrors => {
          acrService.getAuthorisationRequest(invitationId = invitationId).map {
            case Some(info) =>
              Ok(agentCancelInvitationPage(formWithErrors, info))
            case None =>
              throw new RuntimeException(s"Invitation not found for invitationId: $invitationId")
          }
        },
        answer => {
          if (answer) {
            for {
              _ <- acrService.agentCancelInvitation(invitationId)
            } yield Redirect(routes.AgentCancelInvitationController.complete(invitationId))
          } else {
            Future.successful(Redirect(routes.TrackRequestsController.show()))
          }
        }
      )

  def complete(invitationId: String): Action[AnyContent] = actions.agentAuthenticate.async:
    request =>
      given AgentRequest[?] = request
      for {
        info <- acrService.getAuthorisationRequest(invitationId)
          .map(_.getOrElse(throw new RuntimeException(s"Invitation not found for invitationId: $invitationId")))
      } yield Ok(agentCancelInvitationCompletePage(info))
}
