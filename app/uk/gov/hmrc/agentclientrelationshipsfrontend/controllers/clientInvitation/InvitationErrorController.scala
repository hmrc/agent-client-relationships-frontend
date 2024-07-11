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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.clientInvitation

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class InvitationErrorController @Inject()(
                                      mcc: MessagesControllerComponents)
  extends FrontendController(mcc) {


  def notFound: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show not found"))
  }

  def actionNeeded: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show action needed"))
  }

  def cannotViewRequest: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show cannot view request"))
  }

  def noOutstandingRequests: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show no outstanding requests"))
  }

  def authorisationRequestInvalid: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show authorisation request invalid"))
  }

  def authorisationRequestUnsuccessful: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show authorisation request unsuccessful"))
  }

  def cannotFindRequest: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show cannot find request"))
  }

  def incorrectlyAuthorisedAsAgent: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show not found"))
  }

  def showCannotConfirmIdentity(journeyId: Option[String], success: Option[String]): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show cannot confirm identity"))
  }
  def showMissingJourneyHistory: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show missing journey history"))
  }

  def showTrustNotClaimed: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Show trust not claimed"))
  }

  }
