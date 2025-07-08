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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client.routes as clientRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.UrlHelper.validateRedirectUrl
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SignOutController @Inject()(mcc: MessagesControllerComponents)
                                 (implicit appConfig: AppConfig) extends FrontendController(mcc):

  def signOut(continueUrl: Option[RedirectUrl] = None, isAgent: Boolean): Action[AnyContent] = Action.async: _ =>
    val continue = continueUrl match {
      case Some(url) => validateRedirectUrl(url)
      case None if isAgent => appConfig.agentServicesAccountHomeUrl
      case None => appConfig.appExternalUrl + clientRoutes.ManageYourTaxAgentsController.show.url
    }
    val url = url"${appConfig.signOutUrl}?${Map("continue" -> continue)}"

    Future.successful(Redirect(url.toString))

