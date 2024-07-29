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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.agentInvitationFastTrack

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class StartController @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc):
  
  def start: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      // previously in agents-invitations the redirection was informed
      // by the value of query string param "continue" - from sign in
      // this is a placeholder as reproducing involves a lot of dependencies
      Future.successful(Redirect(routes.SelectClientTypeController.show))
