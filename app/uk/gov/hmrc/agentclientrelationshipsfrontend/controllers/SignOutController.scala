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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SignOutController @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc):

  def signOut: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request 
      // previously the destination of sign out was determined by MainTemplate code
      // instead we could do that in here
      Future.successful(Ok("Signed out").withNewSession)
