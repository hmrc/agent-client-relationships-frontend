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

import play.api.http.Status.{NOT_FOUND, OK, SEE_OTHER}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes as journeyRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{InvitationStatus, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.TrackRequestsService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.{Instant, LocalDate}

class TrackRequestsControllerISpec extends ComponentSpecHelper with AuthStubs {
  val arn: String = testArn
  val expiryDate: LocalDate = LocalDate.now().plusDays(10)
  val lastUpdated: Instant = Instant.now()
  def trackRequestsUrl(pageNumber: Int, statusFilter: Option[String], clientName: Option[String]): String =
    s"/agent-client-relationships/agent/$arn/authorisation-requests\\?pageNumber=$pageNumber&pageSize=10" +
      statusFilter.fold("")(status => s"&statusFilter=$status") +
      clientName.fold("")(client => s"&clientName=$client")
  def invitation(status: InvitationStatus, service: String): Invitation =
    Invitation("anyInvitationId", service, expiryDate, "Any Client", status, Instant.now())
  def testTrackRequestsResponseJson(): JsObject = Json.obj(
    "pageNumber" -> 1,
    "requests" -> Json.arr(Json.toJson(invitation(Pending, "HMRC-MTD-IT"))),
    "clientNames" -> Json.arr("Any Client"),
    "availableFilters" -> Json.arr("Pending"),
    "filtersApplied" -> Json.obj(),
    "totalResults" -> 1
  )
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

  def clientDetailsForDeAuthResponseJson(): JsObject = Json.obj(
    "name" -> "anything",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr("anything"),
    "knownFactType" -> "PostalCode",
    "hasPendingInvitation" -> false,
    "hasExistingRelationshipFor" -> "HMRC-MTD-IT"
  )

  def clientDetailsResponseJson(isTrust: Boolean): JsObject = if isTrust then Json.obj(
    "name" -> "anything",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr(),
    "hasPendingInvitation" -> false
  ) else Json.obj(
    "name" -> "anything",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr("anything"),
    "knownFactType" -> "PostalCode",
    "hasPendingInvitation" -> false
  )

  val trackRequestsService: TrackRequestsService = app.injector.instanceOf[TrackRequestsService]

  "GET /manage-authorisation-requests" should {
    "return the manage authorisation requests page" in {
      authoriseAsAgent()
      stubGet(trackRequestsUrl(1, None, None), OK, testTrackRequestsResponseJson().toString)
      val result = get(routes.TrackRequestsController.show(1).url)
      result.status shouldBe OK
    }
  }

  "POST /manage-authorisation-requests" should {
    "return the manage authorisation requests page when submitting filters" in {
      authoriseAsAgent()
      stubGet(trackRequestsUrl(1, Some("Pending"), None), OK, testTrackRequestsResponseJson().toString)
      val result = post(routes.TrackRequestsController.submitFilters.url)(Map("statusFilter" -> Seq("Pending")))
      result.status shouldBe OK
    }
  }

  "GET /manage-authorisation-requests/resend/:invitationId" should {
    "return the resend invitation link page when requested with a valid invitation id" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      val result = get(routes.TrackRequestsController.resendInvitation(invitationId).url)
      result.status shouldBe OK
    }
    "return Page Not Found when non-existent invitation id used in url" in {
      authoriseAsAgent()
      val invitationId = "nonExistentInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", 404, "")
      val result = get(routes.TrackRequestsController.resendInvitation(invitationId).url)
      result.status shouldBe NOT_FOUND
    }
  }

  "GET /manage-authorisation-requests/restart/:invitationId" should {
    "redirect us into the authorisation request journey at known fact stage when known fact required" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      stubGet(s"/agent-client-relationships/client/HMRC-MTD-IT/details/AB123456C", OK, clientDetailsResponseJson(false).toString)
      val result = get(routes.TrackRequestsController.restartInvitation(invitationId).url)
      result.status shouldBe SEE_OTHER
      result.header("location").value shouldBe journeyRoutes.EnterClientFactController.show(AgentJourneyType.AuthorisationRequest).url
    }
    "redirect us into the authorisation request journey at confirm client stage when known fact not required" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      stubGet(s"/agent-client-relationships/client/HMRC-MTD-IT/details/AB123456C", OK, clientDetailsResponseJson(true).toString)
      val result = get(routes.TrackRequestsController.restartInvitation(invitationId).url)
      result.status shouldBe SEE_OTHER
      result.header("location").value shouldBe journeyRoutes.ConfirmClientController.show(AgentJourneyType.AuthorisationRequest).url
    }
  }

  "GET /manage-authorisation-requests/deauth/:invitationId" should {
    "redirect us into the cancel authorisation request journey at confirm cancellation stage" in {
      authoriseAsAgent()
      val invitationId = "someInvitationId"
      stubGet(s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$invitationId", OK, authorisationRequestInfoResponseJson().toString)
      stubGet(s"/agent-client-relationships/client/HMRC-MTD-IT/details/AB123456C", OK, clientDetailsForDeAuthResponseJson().toString)
      val result = get(routes.TrackRequestsController.deAuthFromInvitation(invitationId).url)
      result.status shouldBe SEE_OTHER
      result.header("location").value shouldBe journeyRoutes.CheckYourAnswersController.show(AgentJourneyType.AgentCancelAuthorisation).url
    }
  }

}
