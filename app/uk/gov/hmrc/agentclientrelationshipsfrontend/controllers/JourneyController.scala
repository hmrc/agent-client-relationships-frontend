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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models._
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientServiceConfigurationService, JourneyService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.createInvitation.confirm_client
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JourneyController @Inject()(mcc: MessagesControllerComponents,
                                  journeyService: JourneyService
                                            )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc):
  def route(action: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      
      Redirect(journeyService.getNextRequiredStep(action))
      
  def fastTrack(action: String): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      journeyService.populateJourneyWithFastTrack(request.body.asFormUrlEncoded).flatMap(_ =>
        // TODO: validate request body and return bad requests etc where needed instead of always success
        Future.successful(Redirect(journeyService.getNextRequiredStep(action)))
      )
      
