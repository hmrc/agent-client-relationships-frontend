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

package uk.gov.hmrc.agentclientrelationshipsfrontend.utils

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.NO_CONTENT
import play.api.test.Helpers.{NOT_FOUND, OK}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.{stubGet, stubPost, stubPut}

trait AgentClientRelationshipStub {

  def givenClientRelationshipFor(service: String, clientId: String, body:String): StubMapping = stubGet(
    s"/agent-client-relationships/client/$service/details/$clientId", OK, body)

  def givenNotFoundForServiceAndClient(service: String, clientId: String): StubMapping = stubGet(
    s"/agent-client-relationships/client/$service/details/$clientId", NOT_FOUND, "")

  def givenAcceptAuthorisation(invitationId: String, status: Int): StubMapping = stubPut(
    s"/agent-client-relationships/authorisation-response/accept/$invitationId", status, "")

  def givenRejectAuthorisation(invitationId: String, status: Int): StubMapping = stubPut(
    s"/agent-client-relationships/client/authorisation-response/reject/$invitationId", status, "")

  def givenCancelAuthorisation(arn: String): StubMapping = stubPost(
    s"/agent-client-relationships/agent/$arn/remove-authorisation", NO_CONTENT, "")
}