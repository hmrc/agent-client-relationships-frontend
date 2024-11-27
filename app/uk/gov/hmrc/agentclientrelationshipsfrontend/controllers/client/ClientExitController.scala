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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client

import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.{Actions, ClientRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.client.ClientExitPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ClientExitController @Inject()(mcc: MessagesControllerComponents,
                                     clientExitPage: ClientExitPage,
                                     actions: Actions
                                    ) (implicit val executionContext: ExecutionContext,
                                       appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport:

  def show(exitType: ClientExitType, normalizedAgentName: Option[String] = None, lastModifiedDate: Option[String] = None): Action[AnyContent] = actions.authenticateClient:
    clientRequest =>
      given ClientRequest[?] = clientRequest
        Ok(clientExitPage(
          exitType
        ))
