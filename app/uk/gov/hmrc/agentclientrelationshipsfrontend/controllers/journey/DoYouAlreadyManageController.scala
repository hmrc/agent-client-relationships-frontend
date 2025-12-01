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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.Actions
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.DoYouAlreadyManageForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.agentJourney.DoYouAlreadyManagePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DoYouAlreadyManageController @Inject()(mcc: MessagesControllerComponents,
                                             journeyService: AgentJourneyService,
                                             doYouAlreadyManagePage: DoYouAlreadyManagePage,
                                             actions: Actions
                                            )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport:

  def show(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType):
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      if !journey.eligibleForMapping then Redirect(routes.EnterClientIdController.show(journey.journeyType))
      else
        val clientName = journey.getClientDetailsResponse.name
        val form = journey.alreadyManageAuth match {
          case Some(prepop) => DoYouAlreadyManageForm.form(clientName).fill(prepop)
          case _ => DoYouAlreadyManageForm.form(clientName)
        }
        Ok(doYouAlreadyManagePage(
          form = form
        ))

  def onSubmit(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      if !journey.eligibleForMapping then Future.successful(Redirect(routes.EnterClientIdController.show(journey.journeyType)))
      else
        val clientName = journey.getClientDetailsResponse.name
        DoYouAlreadyManageForm.form(clientName).bindFromRequest().fold(
          formWithErrors => {
            Future.successful(BadRequest(doYouAlreadyManagePage(
              formWithErrors,
            )))
          },
          alreadyManage => {
            journeyService.saveJourney(journey.copy(
              alreadyManageAuth = Some(alreadyManage),
              abortMapping = None
            )).flatMap { _ =>
              if alreadyManage then journeyService.getMappingJourneyUrl.map(Redirect(_))
              else journeyService.nextPageUrl(journeyType).map(Redirect(_))
            }
          }
        )

  def cancelMapping(journeyType: AgentJourneyType): Action[AnyContent] = actions.getAgentJourney(journeyType).async:
    journeyRequest =>
      given AgentJourneyRequest[?] = journeyRequest

      val journey = journeyRequest.journey

      if journey.alreadyManageAuth.isEmpty then Future.successful(Redirect(routes.EnterClientIdController.show(journey.journeyType)))
      else
        journeyService.saveJourney(journey.copy(
          abortMapping = Some(true)
        )).flatMap { _ =>
          journeyService.nextPageUrl(journeyType).map(Redirect(_))
        }