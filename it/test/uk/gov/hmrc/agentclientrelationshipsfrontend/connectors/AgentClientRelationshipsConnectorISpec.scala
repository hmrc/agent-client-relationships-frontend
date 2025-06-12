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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.SubmissionResponse.{SubmissionLocked, SubmissionSuccess}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientType.personal
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.invitationLink.{AgentNotFoundError, AgentSuspendedError, ValidateLinkPartsResponse}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyRequest, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, ComponentSpecHelper}
import uk.gov.hmrc.http.{HeaderCarrier, JsValidationException, UpstreamErrorResponse}

import java.time.{Instant, LocalDate}

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

  val mytaData: ManageYourTaxAgentsData = ManageYourTaxAgentsData(
    agentsInvitations = AgentsInvitationsResponse(agentsInvitations = Seq(
      AgentData(
        uid = "NBM9TUDA",
        agentName = "Test Agent",
        invitations = Seq(
          Invitation(
            invitationId = "1234567890",
            service = "HMRC-MTD-IT",
            clientName = "Some test",
            status = Pending,
            expiryDate = LocalDate.now().plusDays(6),
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "123LD67891",
            service = "HMRC-PILLAR2-ORG",
            clientName = "Some test",
            status = Pending,
            expiryDate = LocalDate.now().plusDays(15),
            lastUpdated = Instant.now()
          )
        )
      ),
      AgentData(
        uid = "1234590",
        agentName = "Test VAT Agent Ltd",
        invitations = Seq(
          Invitation(
            invitationId = "123456BFX90",
            service = "HMRC-MTD-VAT",
            clientName = "Some test",
            status = Pending,
            expiryDate = LocalDate.now().plusDays(11),
            lastUpdated = Instant.now()
          )
        )
      )
    )),
    agentsAuthorisations = AgentsAuthorisationsResponse(agentsAuthorisations = Seq.empty),
    authorisationEvents = AuthorisationEventsResponse(authorisationEvents = Seq.empty)
  )

  val testAuthRequestInfoJson: JsObject = Json.obj(
    "authorisationRequest" -> Json.obj(
      "clientType" -> "personal",
      "invitationId" -> "invitationId",
      "service" -> "HMRC-MTD-IT",
      "clientName" -> "testClientName",
      "suppliedClientId" -> "AB123456C",
      "expiryDate" -> LocalDate.now().plusDays(10),
      "agencyEmail" -> "test@email.com"
    ),
    "agentLink" -> Json.obj(
      "uid" -> "ABCDEF",
      "normalizedAgentName" -> "testName"
    )
  )
  val testAuthRequestInfo: AuthorisationRequestInfo = AuthorisationRequestInfo(
    "invitationId",
    "ABCDEF",
    "testName",
    "test@email.com",
    "personal",
    "testClientName",
    "AB123456C",
    "HMRC-MTD-IT",
    LocalDate.now().plusDays(10)
  )
  val testAuthRequestInfoForClient: AuthorisationRequestInfoForClient = AuthorisationRequestInfoForClient(
    "testName",
    "HMRC-MTD-IT",
    Pending
  )
  val testAgentDetailsJson: JsObject = Json.obj(
    "agencyDetails" -> Json.obj(
      "agencyName" -> "testName",
      "agencyEmail" -> "test@email.com"
    )
  )
  val testAgentDetails: AgentDetails = AgentDetails(
    "testName",
    "test@email.com"
  )
  val testValidateInvitationResponse: ValidateInvitationResponse = ValidateInvitationResponse(
    "invitationId",
    "HMRC-MTD-IT",
    "testName",
    Pending,
    Instant.now(),
    None,
    Some(personal)
  )
  val testTrackRequest: TrackRequestsResult = TrackRequestsResult(
    1,
    Seq(Invitation(
      invitationId = "invitationId",
      service = "HMRC-MTD-IT",
      expiryDate = LocalDate.now(),
      clientName = "testName",
      status = Pending,
      lastUpdated = Instant.now()
    )),
    Seq("testName"),
    Seq("filter"),
    None,
    10
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

  "createAuthorisationRequest" should :
    "return invitationId on success" in :
      givenCreateAuthorisationRequest(testArn, CREATED, Json.obj("invitationId" -> "testId").toString)

      given AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)

      await(testConnector.createAuthorisationRequest(basicJourney)) shouldEqual "testId"
    "throw exception on unexpected response" in :
      givenCreateAuthorisationRequest(testArn, INTERNAL_SERVER_ERROR, Json.obj().toString)

      given AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)

      intercept[RuntimeException](await(testConnector.createAuthorisationRequest(basicJourney)))

  "removeAuthorisation" should :
    "return SubmissionSuccess when a 204 status is received" in :
      givenRemoveAuthorisation(testArn, NO_CONTENT)

      await(testConnector.removeAuthorisation(testClientId, "HMRC-MTD-IT", testArn)) shouldEqual SubmissionSuccess

    "return SubmissionLocked when a 423 status is received" in :
      givenRemoveAuthorisation(testArn, LOCKED)

      await(testConnector.removeAuthorisation(testClientId, "HMRC-MTD-IT", testArn)) shouldEqual SubmissionLocked

    "throw exception on an unexpected response" in :
      givenRemoveAuthorisation(testArn, INTERNAL_SERVER_ERROR)

      intercept[RuntimeException](await(testConnector.removeAuthorisation(testClientId, "HMRC-MTD-IT", testArn)))

  "getAuthorisationRequest" should :
    "return the authorisation response if found" in :
      givenGetAuthorisationRequest(testArn, "invitationId", OK, testAuthRequestInfoJson.toString)

      given AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)

      await(testConnector.getAuthorisationRequest("invitationId")) shouldEqual Some(testAuthRequestInfo)
    "return None if not found" in :
      givenGetAuthorisationRequest(testArn, "invitationId", NOT_FOUND, Json.obj().toString)

      given AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)

      await(testConnector.getAuthorisationRequest("invitationId")) shouldEqual None


  "getAuthorisationRequestForClient" should :
    "return the authorisation response if found" in :
      givenGetAuthorisationRequestForClient("invitationId", OK, Json.toJson(testAuthRequestInfoForClient).toString)

      await(testConnector.getAuthorisationRequestForClient("invitationId")) shouldEqual Some(testAuthRequestInfoForClient)
    "return None if not found" in :
      givenGetAuthorisationRequestForClient("invitationId", NOT_FOUND, Json.obj().toString)

      await(testConnector.getAuthorisationRequestForClient("invitationId")) shouldEqual None

  "getAgentDetails" should :
    "return the agent details if found" in :
      givenGetAgentDetails(testArn, OK, Json.toJson(testAgentDetailsJson).toString)

      given AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)

      await(testConnector.getAgentDetails()) shouldEqual Some(testAgentDetails)
  "return None if not found" in :
    givenGetAgentDetails(testArn, NOT_FOUND, Json.obj().toString)

    given AgentJourneyRequest[?] = new AgentJourneyRequest(testArn, basicJourney, request)

    await(testConnector.getAgentDetails()) shouldEqual None

  "trackRequests" should :
    "return TrackRequestsResult when found" in :
      givenTrackRequests(testArn, 2, 10, Some("pending"), Some("testName"), OK, Json.toJson(testTrackRequest).toString)

      await(testConnector.trackRequests(testArn, 2, Some("pending"), Some("testName"))) shouldEqual testTrackRequest
    "throw exception on unexpected response" in :
      givenTrackRequests(testArn, 2, 10, Some("pending"), Some("testName"), INTERNAL_SERVER_ERROR, Json.obj().toString)

      intercept[Exception](await(testConnector.trackRequests(testArn, 2, Some("pending"), Some("testName"))))

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

  "acceptAuthorisation" should :
    "return SubmissionSuccess when a 204 status is received" in :
      givenAcceptAuthorisation("ABC123", NO_CONTENT)
      await(testConnector.acceptAuthorisation("ABC123")) shouldEqual SubmissionSuccess

    "return SubmissionLocked when a 423 status is received" in :
      givenAcceptAuthorisation("ABC123", LOCKED)
      await(testConnector.acceptAuthorisation("ABC123")) shouldEqual SubmissionLocked

    "throw an exception when a non-204 status is received" in :
      givenAcceptAuthorisation("ABC123", INTERNAL_SERVER_ERROR)
      intercept[Exception](await(testConnector.acceptAuthorisation("ABC123")))

  "rejectAuthorisation" should :
    "return nothing when a 204 status is received" in :
      givenRejectAuthorisation("ABC123", NO_CONTENT)
      await(testConnector.rejectAuthorisation("ABC123")) shouldEqual()

    "throw an exception when a non-204 status is received" in :
      givenRejectAuthorisation("ABC123", INTERNAL_SERVER_ERROR)
      intercept[Exception](await(testConnector.rejectAuthorisation("ABC123")))

  "cancelInvitation" should :
    "return nothing when a 204 status is received" in :
      givenCancelInvitation("ABC123", NO_CONTENT)
      await(testConnector.cancelInvitation("ABC123")) shouldEqual()

    "throw an exception when a non-204 status is received" in :
      givenCancelInvitation("ABC123", INTERNAL_SERVER_ERROR)
      intercept[Exception](await(testConnector.cancelInvitation("ABC123")))

  "validateInvitation" should :
    "return ValidInvitationResponse on Ok" in :
      givenValidateInvitation(OK, Json.toJson(testValidateInvitationResponse).toString)
      await(testConnector.validateInvitation("testUid", Set("HMRC-MTD-IT"))) shouldEqual Right(testValidateInvitationResponse)

    "return AgentSuspendedError on Forbidden" in :
      givenValidateInvitation(FORBIDDEN, Json.obj().toString)
      await(testConnector.validateInvitation("testUid", Set("HMRC-MTD-IT"))) shouldEqual Left(InvitationAgentSuspendedError)

    "return InvitationOrAgentNotFoundError on Not Found" in :
      givenValidateInvitation(NOT_FOUND, Json.obj().toString)
      await(testConnector.validateInvitation("testUid", Set("HMRC-MTD-IT"))) shouldEqual Left(InvitationOrAgentNotFoundError)

    "throw Exception on unexpected response" in :
      givenValidateInvitation(INTERNAL_SERVER_ERROR, Json.obj().toString)
      intercept[Exception](await(testConnector.validateInvitation("testUid", Set("HMRC-MTD-IT"))))

  "getManageYourTaxAgentsData" should :
    "return the Manage Your Tax Agents data when receiving a 200 response with a valid json" in :
      stubGet("/agent-client-relationships/client/authorisations-relationships", OK, Json.toJson(mytaData).toString)
      val result = testConnector.getManageYourTaxAgentsData()
      await(result) shouldBe mytaData

}
