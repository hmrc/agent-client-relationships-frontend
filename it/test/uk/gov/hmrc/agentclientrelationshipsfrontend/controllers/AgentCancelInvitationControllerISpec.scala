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

import play.api.http.Status.{NO_CONTENT, OK, SEE_OTHER}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.{stubGet, stubPut}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.LocalDate

class AgentCancelInvitationControllerISpec extends ComponentSpecHelper with AuthStubs {
  val arn: String = testArn
  val expiryDate: LocalDate = LocalDate.now().plusDays(10)

  def authorisationRequestInfoResponseJson(): JsObject = Json.obj(
    "authorisationRequest" -> Json.obj(
      "clientType" -> "personal",
      "invitationId" -> "someInvitationId",
      "service" -> "HMRC-MTD-IT",
      "clientName" -> "Any Client",
      "suppliedClientId" -> "AB123456C",
      "expiryDate" -> expiryDate,
      "agencyEmail" -> "agent@example.com"
    ),
    "agentLink" -> Json.obj(
      "uid" -> "ABCDEF",
      "normalizedAgentName" -> "agent-name-1"
    )
  )

  "GET /manage-authorisation-requests/cancel/:invitationId" should {
    "return the cancel invitation page" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      val result = get(routes.AgentCancelInvitationController.show(invitationId).url)
      result.status shouldBe OK
    }
  }

  "POST /manage-authorisation-requests/confirm-cancellation/:invitationId" should {
    "redirect us to the cancel invitation complete page when we confirm cancellation" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      stubPut(s"/agent-client-relationships/agent/cancel-invitation/$invitationId", NO_CONTENT, "")
      val result = post(routes.AgentCancelInvitationController.submit(invitationId).url)(Map("agentCancelInvitation" -> Seq("true")))
      result.status shouldBe SEE_OTHER
      result.header("location").value shouldBe routes.AgentCancelInvitationController.complete(invitationId).url
    }
    "redirect us to back to the track requests page when we answer NO to cancellation" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      stubPut(s"/agent-client-relationships/agent/cancel-invitation/$invitationId", NO_CONTENT, "")
      val result = post(routes.AgentCancelInvitationController.submit(invitationId).url)(Map("agentCancelInvitation" -> Seq("false")))
      result.status shouldBe SEE_OTHER
      result.header("location").value shouldBe routes.TrackRequestsController.show().url
    }
  }

  "GET /manage-authorisation-requests/confirm-cancellation/complete/:invitationId" should {
    "return the cancel invitation complete page" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      val result = get(routes.AgentCancelInvitationController.complete(invitationId).url)
      result.status shouldBe OK
    }
  }

}
