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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.testOnly

import play.api.{Environment, Mode}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, MessagesRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.testOnly.{TestOnlyAsaDashboard, TestOnlyFastTrack}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.testOnly.TestOnlyFastTrackForm

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TestOnlyController @Inject()(mcc: MessagesControllerComponents, 
                                   view: TestOnlyAsaDashboard,
                                   testFastTrackView: TestOnlyFastTrack,
                                   serviceConfig: ClientServiceConfigurationService,
                                   val env: Environment
                                  ) extends FrontendController(mcc):

  def fakeDashboard: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request 
      // previously the destination of sign out was determined by MainTemplate code
      // instead we could do that in here
      Future.successful(Ok(view()))

  private val isLocalEnv = if (env.mode.equals(Mode.Test)) false else env.mode.equals(Mode.Dev)

  def getFastTrackForm: Action[AnyContent] = Action.async { implicit request =>
    Future successful Ok(testFastTrackView(TestOnlyFastTrackForm.form(serviceConfig.allClientTypes, serviceConfig.allSupportedServices), isLocalEnv))
  }



