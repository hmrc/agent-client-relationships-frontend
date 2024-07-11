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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentFastTrackErrorController @Inject()(
                                      mcc: MessagesControllerComponents
                                              )
  extends FrontendController(mcc) {
    def showClientNotSignedUp: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show client not signed up"))
    }

    def showClientNotRegistered: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show client not registered"))
    }

    def showNotMatched: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show not matched"))
    }

    def redirectTryAgainNotMatchedKnownFact: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show redirect try again known fact not matched"))
    }

    def showPendingAuthorisationExists: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show fast track pending authorisation exists"))
    }

    def showActiveAuthorisationExists: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show fast track active authorisation exists"))
    }

    def showAgentSuspended: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show agent fast track suspended"))
    }

    def showAlreadyCopiedAcrossItsa: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show fast track already coped across ITSA"))
    }

    def showClientInsolvent: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show fast track client insolvent"))
    }

    def showCannotCreateFastTrackRequest: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Submit cannot create fast track request"))
    }

}
