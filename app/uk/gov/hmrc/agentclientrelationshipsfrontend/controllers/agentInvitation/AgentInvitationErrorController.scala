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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.agentInvitation

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentInvitationErrorController @Inject()(
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

    def showCannotCreateRequest: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show cannot create request"))
    }

    def showAllAuthorisationsRemoved: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show all authorisations removed"))
    }

    def showPendingAuthorisationExists: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show pending authorisation exists"))
    }

    def showActiveAuthorisationExists: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show active authorisation exists"))
    }

    def showAllAuthorisationsFailed: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show all authorisations failed"))
    }

    def showSomeAuthorisationsFailed: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show some authorisations removed"))
    }

    def submitSomeAuthorisationsFailed: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Submit some authorisations failed"))
    }

    def showAgentSuspended: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show agent suspended"))
    }

    def showAlreadyCopiedAcrossItsa: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show already coped across ITSA"))
    }

    def showClientInsolvent: Action[AnyContent] = Action.async { implicit request =>
        Future.successful(Ok("Show client insolvent"))
    }

}
