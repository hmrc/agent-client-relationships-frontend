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
import uk.gov.hmrc.agentclientrelationshipsfrontend.auth.AuthActions
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestAuthController @Inject()(mcc: MessagesControllerComponents,
                                   authActions: AuthActions)
                                  (implicit executionContext: ExecutionContext)
  extends FrontendController(mcc):

  def agent: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      authActions.authorisedAsAgent { agent =>
        Future.successful(Ok(s"Auth passed for :$agent"))
      }

  def client: Action[AnyContent] = Action.async:
    request =>
      given MessagesRequest[AnyContent] = request

      authActions.authorisedAsClient(Some("id")) { client =>
        Future.successful(Ok(s"Auth passed for :${client.toString}"))
      }