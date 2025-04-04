/*
 * Copyright 2025 HM Revenue & Customs
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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import play.api.http.Status.*
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.DeclineRequestFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubPost
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl


class DeclineRequestControllerISpec extends ComponentSpecHelper with AuthStubs with AgentClientRelationshipStub:

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val testUid = "ABCD"

  val taxServices: Map[String, String] = Map(
    "income-tax" -> "HMRC-MTD-IT",
    "income-record-viewer" -> "HMRC-PT",
    "vat" -> "HMRC-MTD-VAT",
    "capital-gains-tax-uk-property" -> "HMRC-CGT-PD",
    "plastic-packaging-tax" -> "HMRC-PPT-ORG",
    "country-by-country-reporting" -> "HMRC-CBC-ORG",
    "pillar-2" -> "HMRC-PILLAR2-ORG",
    "trusts-and-estates" -> "HMRC-TERS-ORG",
  )

  val defaultTaxService: String = "income-tax"

  val validateInvitationUrl = s"/agent-client-relationships/client/validate-invitation"

  def testValidateInvitationResponseJson(taxService: String, status: String = "Pending"): JsObject = Json.obj(
    "invitationId" -> "AB1234567890",
    "serviceKey" -> taxService,
    "agentName" -> "Pep Guardi",
    "status" -> status,
    "lastModifiedDate" -> "2024-12-01T12:00:00Z",
    "existingMainAgent" -> Json.obj(
      "agencyName" -> "CFG Solutions",
      "sameAgent" -> true
    ),
    "clientType" -> "personal"
  )

  val serviceConfigurationService: ClientServiceConfigurationService = app.injector.instanceOf[ClientServiceConfigurationService]
  val agentClientRelationshipsConnector: AgentClientRelationshipsConnector = app.injector.instanceOf[AgentClientRelationshipsConnector]
  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(journeyService.deleteAllAnswersInSession(request))
  }

  "GET /authorisation-response/:uid/:taxService/confirm-decline" should :

    "redirect to a InsufficientEnrolments exit page when the URL part is not valid for the user enrolments" in :
      authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
      journeyService
        .saveJourney(
          journey = ClientJourney(
            journeyType = "authorisation-response"
          )
        ).futureValue
      val result = get(
        uri = routes.DeclineRequestController.show(
          uid = testUid,
          taxService = "vat"
        ).url
      )
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController
        .showClient(
          exitType = ClientExitType.CannotFindAuthorisationRequest,
          continueUrl = Some(RedirectUrl(appConfig.appExternalUrl + routes.DeclineRequestController.show(
            uid = testUid,
            taxService = "vat"
          ).url))
        ).url

    "redirect to NoOutstandingRequests exit page when the invitation data is not found" in :
      authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
      journeyService
        .saveJourney(journey = ClientJourney(
          journeyType = "authorisation-response",
          serviceKey = Some("HMRC-MTD-IT")
        )).futureValue
      stubPost(
        url = validateInvitationUrl,
        status = NOT_FOUND,
        responseBody = ""
      )
      val result = get(uri = routes.DeclineRequestController.show(testUid, defaultTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.showClient(
        exitType = ClientExitType.NoOutstandingRequests,
        taxService = Some(defaultTaxService)
      ).url

    "redirect to AgentSuspended exit page when the agent is suspended" in :
      authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
      journeyService
        .saveJourney(journey = ClientJourney(
          journeyType = "authorisation-response",
          serviceKey = Some("HMRC-MTD-IT")
        )).futureValue
      stubPost(validateInvitationUrl, FORBIDDEN, "")
      val result = get(
        uri = routes.DeclineRequestController.show(
          uid = testUid,
          taxService = defaultTaxService
        ).url
      )
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.showClient(
        exitType = ClientExitType.AgentSuspended,
        taxService = Some(defaultTaxService)
      ).url

    taxServices.keySet.foreach: taxService =>
      val serviceKey: String = taxServices(taxService)
      s"display consent information page for $taxService" in :
        authoriseAsClientWithEnrolments(enrolment = serviceKey)
        journeyService
          .saveJourney(journey = ClientJourney(
            journeyType = "authorisation-response",
            serviceKey = Some(serviceKey)
          )).futureValue
        stubPost(validateInvitationUrl, OK, testValidateInvitationResponseJson(serviceKey).toString())
        val result = get(
          uri = routes.DeclineRequestController.show(
            uid = testUid,
            taxService = taxService
          ).url
        )
        result.status shouldBe OK

      s"redirect to expired exit page for $taxService" in :
        authoriseAsClientWithEnrolments(enrolment = serviceKey)
        journeyService
          .saveJourney(journey = ClientJourney(
            journeyType = "authorisation-response",
            serviceKey = Some(serviceKey)
          )).futureValue
        stubPost(
          url = validateInvitationUrl,
          status = OK,
          responseBody = testValidateInvitationResponseJson(
            taxService = serviceKey,
            status = "Expired"
          ).toString())
        val result = get(
          uri = routes.DeclineRequestController.show(
            uid = testUid,
            taxService = taxService
          ).url
        )
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(
          exitType = ClientExitType.AuthorisationRequestExpired
        ).url

      s"redirect to cancelled exit page for $taxService" in :
        authoriseAsClientWithEnrolments(enrolment = serviceKey)
        journeyService
          .saveJourney(journey = ClientJourney(
            journeyType = "authorisation-response",
            serviceKey = Some(serviceKey)
          )).futureValue
        stubPost(
          url = validateInvitationUrl,
          status = OK,
          responseBody = testValidateInvitationResponseJson(
            taxService = serviceKey,
            status = "Cancelled"
          ).toString())
        val result = get(
          uri = routes.DeclineRequestController.show(
            uid = testUid,
            taxService = taxService
          ).url
        )
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(
          exitType = ClientExitType.AuthorisationRequestCancelled
        ).url

      s"redirect to already accepted exit page for $taxService" in :
        authoriseAsClientWithEnrolments(enrolment = serviceKey)
        journeyService
          .saveJourney(journey = ClientJourney(
            journeyType = "authorisation-response",
            serviceKey = Some(serviceKey)
          )).futureValue
        stubPost(
          url = validateInvitationUrl,
          status = OK,
          responseBody = testValidateInvitationResponseJson(
            taxService = serviceKey,
            status = "Accepted"
          ).toString()
        )
        val result = get(
          uri = routes.DeclineRequestController.show(
            uid = testUid,
            taxService = taxService
          ).url
        )
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(
          exitType = ClientExitType.AlreadyAcceptedAuthorisationRequest
        ).url

      s"redirect to already refused exit page for $taxService" in :
        authoriseAsClientWithEnrolments(enrolment = serviceKey)
        journeyService
          .saveJourney(journey = ClientJourney(
            journeyType = "authorisation-response",
            serviceKey = Some(serviceKey)
          )).futureValue
        stubPost(
          url = validateInvitationUrl,
          status = OK,
          responseBody = testValidateInvitationResponseJson(
            taxService = serviceKey,
            status = "Rejected"
          ).toString()
        )
        val result = get(
          uri = routes.DeclineRequestController.show(
            uid = testUid,
            taxService = taxService
          ).url
        )
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(
          ClientExitType.AlreadyRefusedAuthorisationRequest
        ).url


  "POST /authorisation-response/:uid/:taxService/confirm-decline" when :

    val baseJourneyModel = ClientJourney(
      journeyType = "authorisation-response",
      serviceKey = Some("HMRC-MTD-IT"),
      agentName = Some("Name"),
      invitationId = Some("ABC123")
    )

    "the user selects 'Yes' to confirm their rejection" should :

      "reject the invitation and redirect to the Confirmation page" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(journey = baseJourneyModel).futureValue
        givenRejectAuthorisation("ABC123", NO_CONTENT)
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("true")))
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show.url

      "return BadRequest when there is no service in session" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(journey = baseJourneyModel.copy(serviceKey = None)).futureValue
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("true")))
        result.status shouldBe BAD_REQUEST

      "return BadRequest when there is no agent name in session" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(journey = baseJourneyModel.copy(agentName = None)).futureValue
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("true")))
        result.status shouldBe BAD_REQUEST

      "return BadRequest when there is no invitation ID in session" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(
          journey = baseJourneyModel.copy(invitationId = None)
        ).futureValue
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("true")))
        result.status shouldBe BAD_REQUEST

    "the user selects 'No' to consider giving consent" should :

      "redirect to the ConsentInformation page" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(journey = baseJourneyModel).futureValue
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("false")))
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConsentInformationController.show(
          uid = testUid,
          taxService = defaultTaxService
        ).url

      "return BadRequest when there is no service in session" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(journey = baseJourneyModel.copy(serviceKey = None)).futureValue
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("false")))
        result.status shouldBe BAD_REQUEST

      "return BadRequest when there is no agent name in session" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        journeyService.saveJourney(journey = baseJourneyModel.copy(agentName = None)).futureValue
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("false")))
        result.status shouldBe BAD_REQUEST

      "return BadRequest when there is no invitation ID in session" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        await(journeyService.saveJourney(journey = baseJourneyModel.copy(invitationId = None)))
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("false")))
        result.status shouldBe BAD_REQUEST

    "the user does not select an option" should :

      "return BadRequest" in :
        authoriseAsClientWithEnrolments(enrolment = "HMRC-MTD-IT")
        await(journeyService.saveJourney(journey = baseJourneyModel))
        val result = post(
          uri = routes.DeclineRequestController.submit(
            uid = testUid,
            taxService = defaultTaxService
          ).url
        )(Map(DeclineRequestFieldName -> Seq("")))
        result.status shouldBe BAD_REQUEST
