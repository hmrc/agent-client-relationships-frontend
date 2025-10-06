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

import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{Accepted, InvitationStatus, PartialAuth, Rejected}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class ConfirmationControllerISpec extends ComponentSpecHelper with AuthStubs :

  val testInvitationId: String = "AB1234567890"

  def getAuthorisationRequestUrl = s"/agent-client-relationships/client/authorisation-request-info/$testInvitationId"

  def getAgentDetailsUrl = s"/agent-client-relationships/agent/$testArn/details"

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

  def testGetAuthorisationRequestInfoResponse(service: String, status: InvitationStatus): JsObject = Json.obj(
    "agentName" -> agentName,
    "service" -> service,
    "status" -> status.toString
  )

  private val completeJourney: ClientJourney = ClientJourney(
    "authorisation-response",
    journeyComplete = Some(testInvitationId),
    invitationAccepted = Some(true)
  )

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-response/confirmation" should :

    "redirect to MYTA when no journey session present" in :
      authoriseAsClient()
      val result = get(routes.ConfirmationController.show.url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "/agent-client-relationships/manage-your-tax-agents"

    List(Accepted, Rejected, PartialAuth).foreach(decision => s"display the confirmation page for ${decision.toString} when the invitation id is in journeyComplete" in :
      authoriseAsClientWithEnrolments("HMRC-MTD-IT")
      stubGet(getAuthorisationRequestUrl, OK, testGetAuthorisationRequestInfoResponse("HMRC-MTD-IT", decision).toString())
      await(journeyService.saveJourney(completeJourney))
      val result = get(routes.ConfirmationController.show.url)
      result.status shouldBe OK
    )

    "return 500 when ACR does not return an invitation for the invitationId in session" in :
      authoriseAsClient()
      stubGet(getAuthorisationRequestUrl, NOT_FOUND, "")
      await(journeyService.saveJourney(completeJourney))
      val result = get(routes.ConfirmationController.show.url)
      result.status shouldBe INTERNAL_SERVER_ERROR
