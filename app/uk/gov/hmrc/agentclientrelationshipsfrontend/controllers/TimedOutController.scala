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

import play.api.mvc.*
import sttp.model.Uri.UriContext
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.UrlHelper.validateRedirectUrl
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.UserTimedOut
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TimedOutController @Inject()(
                                    timedOutView: UserTimedOut,
                                    mcc: MessagesControllerComponents
                                  )(implicit appConfig: AppConfig) extends FrontendController(mcc):

  def doTimeOut(continueUrl: RedirectUrl, serviceHeader: String, isAgent: Boolean): Action[AnyContent] = Action.async:
    request =>
      val timedOutPageUrl = uri"${appConfig.appExternalUrl + routes.TimedOutController.timedOut(continueUrl, serviceHeader, isAgent).url}"
      val doTimeOutUrl = uri"${appConfig.signOutUrl}?${Map("continue" -> timedOutPageUrl)}"
      Future.successful(Redirect(doTimeOutUrl.toString))

  def timedOut(continueUrl: RedirectUrl, serviceHeader: String, isAgent: Boolean): Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request
      Future.successful(Ok(timedOutView(Some(validateRedirectUrl(continueUrl)), Some(serviceHeader), isAgent)))
