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

package uk.gov.hmrc.agentclientrelationshipsfrontend.connectors

import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.{AgentNotFoundError, AgentSuspendedError, ValidateLinkPartsResponse}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyRequest, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, ClientStatus, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, ComponentSpecHelper}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.http.{HeaderCarrier, JsValidationException, UpstreamErrorResponse}

class AgentClientRelationshipsConnectorISpec extends ComponentSpecHelper with AgentClientRelationshipStub {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val testConnector: AgentClientRelationshipsConnector = app.injector.instanceOf[AgentClientRelationshipsConnector]

  def getClientDetailsUrl(service: String, clientId: String) = s"/agent-client-relationships/client/$service/details/$clientId"

  def getValidateLinkResponseUrl(uid: String, normalizedAgentName: String) = s"/agent-client-relationships/agent/agent-reference/uid/$uid/$normalizedAgentName"

  val testArn = "TARN0000001"
  val testClientId = "clientId"
  val testService = "HMRC-MTD-IT"
  val testName = "Test Name"
  val testPostCode = "AA1 1AA"
  val testUid = "ABCD"
  val testNormalizedAgentName = "abc_ltd"
  val testClientDetailsResponse: ClientDetailsResponse = ClientDetailsResponse(
    testName,
    Some(ClientStatus.Insolvent),
    isOverseas = Some(false),
    knownFacts = Seq(testPostCode),
    knownFactType = Some(KnownFactType.PostalCode),
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None
  )
  val testClientDetailsResponseJson: JsObject = Json.obj(
    "name" -> testName,
    "status" -> "Insolvent",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr(testPostCode),
    "knownFactType" -> "PostalCode",
    "hasPendingInvitation" -> false
  )

  private val basicClientDetails = ClientDetailsResponse(testName, None, None, Seq(testPostCode), Some(KnownFactType.PostalCode), false, None)
  private val basicJourney: AgentJourney = AgentJourney(
    journeyType = AgentJourneyType.AgentCancelAuthorisation,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some(testClientId),
    clientDetailsResponse = Some(basicClientDetails),
    knownFact = Some(testPostCode),
    clientConfirmed = Some(true),
    agentType = None
  )

  val testValidateLinkResponse: ValidateLinkPartsResponse = ValidateLinkPartsResponse(
    testName
  )

  val testValidateLinkResponseJson: JsObject = Json.obj(
    "name" -> testName,
  )

  "getClientDetails" should {
    "return the client details when receiving a 200 response with a valid json" in {
      stubGet(getClientDetailsUrl(testService, testClientId), OK, testClientDetailsResponseJson.toString)
      val result = testConnector.getClientDetails(testService, testClientId)
      await(result) shouldBe Some(testClientDetailsResponse)
    }
    "return None when receiving a 404 response" in {
      stubGet(getClientDetailsUrl(testService, testClientId), NOT_FOUND, "")
      val result = testConnector.getClientDetails(testService, testClientId)
      await(result) shouldBe None
    }
    "throw a JsValidationException for for a 200 with an invalid json" in {
      stubGet(getClientDetailsUrl(testService, testClientId), OK, Json.obj("invalid" -> "json").toString)
      val result = testConnector.getClientDetails(testService, testClientId)
      intercept[JsValidationException](await(result))
    }
    "throw a UpstreamErrorResponse for any unexpected response" in {
      stubGet(getClientDetailsUrl(testService, testClientId), INTERNAL_SERVER_ERROR, "")
      val result = testConnector.getClientDetails(testService, testClientId)
      intercept[UpstreamErrorResponse](await(result))
    }
  }

  "validateLinkParts" should {

    "return a Right containing ValidateLinkPartsResponse" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), OK, testValidateLinkResponseJson.toString)
      val result = testConnector.validateLinkParts(testUid, testNormalizedAgentName)
      await(result) shouldBe Right(testValidateLinkResponse)
    }

    "return a Left containing AGENT_NOT_FOUND" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), NOT_FOUND, "")
      val result = testConnector.validateLinkParts(testUid, testNormalizedAgentName)
      await(result) shouldBe Left(AgentNotFoundError)
    }

    "return a Left containing AGENT_SUSPENDED" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), FORBIDDEN, "")
      val result = testConnector.validateLinkParts(testUid, testNormalizedAgentName)
      await(result) shouldBe Left(AgentSuspendedError)
    }

    "return error status if service service unavailable" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), SERVICE_UNAVAILABLE, "")
      intercept[Exception](await(testConnector.validateLinkParts(testUid, testNormalizedAgentName)))
    }
  }

  "acceptAuthorisation" should:

    "return nothing when a 204 status is received" in:
      givenAcceptAuthorisation("ABC123", NO_CONTENT)
      await(testConnector.acceptAuthorisation("ABC123")) shouldEqual ()

    "throw an exception when a non-204 status is received" in:
      givenAcceptAuthorisation("ABC123", INTERNAL_SERVER_ERROR)
      intercept[Exception](await(testConnector.acceptAuthorisation("ABC123")))

  "rejectAuthorisation" should :

    "return nothing when a 204 status is received" in:
      givenRejectAuthorisation("ABC123", NO_CONTENT)
      await(testConnector.rejectAuthorisation("ABC123")) shouldEqual ()

    "throw an exception when a non-204 status is received" in:
      givenRejectAuthorisation("ABC123", INTERNAL_SERVER_ERROR)
      intercept[Exception](await(testConnector.rejectAuthorisation("ABC123")))

  "cancelAuthorisation" should:
    "return nothing when a 204 status is received" in:
      givenCancelAuthorisation(testArn)
      implicit val journeyRequest: AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)
      await(testConnector.cancelAuthorisation(basicJourney)) shouldEqual ()
}
