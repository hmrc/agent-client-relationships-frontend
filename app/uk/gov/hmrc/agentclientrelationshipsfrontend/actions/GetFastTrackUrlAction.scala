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

package uk.gov.hmrc.agentclientrelationshipsfrontend.actions


import play.api.mvc.{ActionFunction, Result}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AgentFastTrackRequestWithRedirectUrls
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.UrlHelper

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetFastTrackUrlAction @Inject()(implicit  appConfig: AppConfig, ec: ExecutionContext) {

  def getFastTrackUrlAction: ActionFunction[AgentRequest, AgentFastTrackRequestWithRedirectUrls] = new ActionFunction[AgentRequest, AgentFastTrackRequestWithRedirectUrls]:
    override protected def executionContext: ExecutionContext = ec
    
    override def invokeBlock[A](request: AgentRequest[A], block: AgentFastTrackRequestWithRedirectUrls[A] => Future[Result]): Future[Result] = {
      val redirectUrl = request.getQueryString("continue").flatMap(UrlHelper.getRedirectUrl(_))
      val errorUrl = request.getQueryString("error").flatMap(UrlHelper.getRedirectUrl(_))
      val refererUrl = request.headers.get("Referer").flatMap(UrlHelper.getRedirectUrl(_))
      block(AgentFastTrackRequestWithRedirectUrls(arn = request.arn, redirectUrl, errorUrl, refererUrl, request))
    }
}