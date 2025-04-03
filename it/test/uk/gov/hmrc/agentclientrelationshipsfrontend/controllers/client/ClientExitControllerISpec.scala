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

import play.api.http.Status.*
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.Instant

class ClientExitControllerISpec extends ComponentSpecHelper with AuthStubs {

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  val alreadyAcceptedStatus: List[InvitationStatus] = List(Accepted, DeAuthorised, PartialAuth)

  val testName = "Test Name"

  def testValidateInvitationResponseJson(taxService: String, status: String = "Pending"): JsObject = Json.obj(
    "invitationId" -> "AB1234567890",
    "serviceKey" -> taxService,
    "agentName" -> testName,
    "status" -> status,
    "lastModifiedDate" -> "2024-12-01T12:00:00Z"
  )

  val validateInvitationUrl = s"/agent-client-relationships/client/validate-invitation"

  def journeyModel(status: Option[InvitationStatus]): ClientJourney = ClientJourney(
    "authorisation-response",
    consent = Some(true),
    serviceKey = Some("HMRC-MTD-IT"),
    invitationId = Some("ABC123"),
    agentName = Some("ABC Accountants"),
    status = status,
    lastModifiedDate = Some(Instant.parse("2024-12-01T12:00:00Z"))
  )

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "the showUnauthenticated action" should {
    "return 200" when {
      "the user is signed out and the agent is suspended" in {
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showUnauthorised(AgentSuspended).url)

        result.status shouldBe OK
      }
      "the user is signed out and there are no outstanding requests" in {
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showUnauthorised(NoOutstandingRequests).url)

        result.status shouldBe OK
      }
      "a client is signed in and the agent is suspended" in {
        authoriseAsClientWithEnrolments("HMRC-MTD-IT")
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showUnauthorised(AgentSuspended).url)

        result.status shouldBe OK
      }
      "a client is signed in and  there are no outstanding requests" in {
        authoriseAsClientWithEnrolments("HMRC-MTD-IT")
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showUnauthorised(NoOutstandingRequests).url)

        result.status shouldBe OK
      }
    }
  }

  "the showClient action" should {
    "return 200" when {
      "the client is authenticated and the authorisation request is Cancelled" in {
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel(Some(Cancelled))))
        val result = get(routes.ClientExitController.showClient(AuthorisationRequestCancelled).url)
        result.status shouldBe OK
      }
      "the client is authenticated and the authorisation request is Expired" in {
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel(Some(Expired))))
        val result = get(routes.ClientExitController.showClient(AuthorisationRequestExpired).url)
        result.status shouldBe OK
      }
      alreadyAcceptedStatus.foreach(status =>
        s"the client is authenticated and the authorisation request is $status" in {
          authoriseAsClient()
          await(journeyService.saveJourney(journeyModel(Some(status))))
          val result = get(routes.ClientExitController.showClient(AlreadyAcceptedAuthorisationRequest).url)

          result.status shouldBe OK
        })

      s"the client is authenticated and the authorisation request is Rejected" in {
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel(Some(Rejected))))
        val result = get(routes.ClientExitController.showClient(AlreadyRefusedAuthorisationRequest).url)

        result.status shouldBe OK
      }
    }
  }
}