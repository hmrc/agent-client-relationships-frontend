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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AgentCancelAuthorisationResponse, AuthorisationRequestInfo}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.LocalDate

class ConfirmationControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testInvitationId: String = "AB1234567890"
  def getAuthorisationRequestUrl = s"/agent-client-relationships/agent/$testArn/authorisation-request-info/$testInvitationId"
  def getAgentDetailsUrl = s"/agent-client-relationships/agent/$testArn/details"
  val testClientName: String = "Test Client"
  val agentName: String = "ABC Accountants"
  val testEmail: String = "abc@example.com"

  val testCreateAuthorisationRequestResponseJson: JsObject = Json.obj(
    "invitationId" -> testInvitationId
  )

  def testGetAgentDetailsResponse: JsObject = Json.obj(
    "agencyDetails" -> Json.obj(
      "agencyName" -> agentName,
      "agencyEmail" -> testEmail
    )
  )

  def testGetAuthorisationRequestInfoResponse(service: String): JsObject = Json.obj(
    "authorisationRequest" -> Json.obj(
      "clientName" -> testClientName,
      "service" -> service,
      "expiryDate" -> LocalDate.now(),
    ),
    "agentLink" -> Json.obj(
      "uid" -> "ABC123",
      "normalizedAgentName" -> "abc-accountants"
    ),
    "agentDetails" -> Json.obj(
      "agencyDetails" -> Json.obj(
        "agencyEmail" -> testEmail
      )
    )
  )

  private val completeCancellationJourney: AgentJourney = AgentJourney(
    JourneyType.AgentCancelAuthorisation,
    journeyComplete = Some("2024-12-25"),
    confirmationClientName = Some(testClientName),
    confirmationService = Some("HMRC-MTD-IT")
  )

  private val completeCreateAuthorisationRequestJourney: AgentJourney = AgentJourney(
    JourneyType.AuthorisationRequest,
    journeyComplete = Some(testInvitationId)
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/confirmation" should {
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.ConfirmationController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "display the confirmation page when the invitation id is in journeyComplete" in {
      authoriseAsAgent()
      stubGet(getAuthorisationRequestUrl, OK, testGetAuthorisationRequestInfoResponse("HMRC-MTD-IT").toString())
      await(journeyService.saveJourney(completeCreateAuthorisationRequestJourney))
      val result = get(routes.ConfirmationController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    }
  }

  "GET /agent-cancel-authorisation/confirmation" should {
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.ConfirmationController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "display the the confirmation page when the authorisation has been cancelled" in {
      authoriseAsAgent()
      stubGet(getAgentDetailsUrl, OK, testGetAgentDetailsResponse.toString())
      await(journeyService.saveJourney(completeCancellationJourney))
      val result = get(routes.ConfirmationController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe OK
    }
  }

}
