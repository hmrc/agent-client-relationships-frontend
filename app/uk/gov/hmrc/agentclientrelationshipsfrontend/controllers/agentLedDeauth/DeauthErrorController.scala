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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.agentLedDeauth

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class DeauthErrorController @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc):
  
  def notSignedUp: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      Future.successful(Ok("Show deauth not signed up"))

  def notAuthorised: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      Future.successful(Ok("Show deauth not authorised"))

  def knownFactNotMatched: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      Future.successful(Ok("Show deauth known fact not matched"))

  def responseFailed: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      Future.successful(Ok("Show deauth response failed"))
