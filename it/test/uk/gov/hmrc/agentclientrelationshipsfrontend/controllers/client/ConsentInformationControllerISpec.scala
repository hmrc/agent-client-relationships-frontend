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

import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{ClientJourneyService, ClientServiceConfigurationService}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubPost
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class ConsentInformationControllerISpec extends ComponentSpecHelper with ScalaFutures with AuthStubs {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val testUid = "ABCD"
  val testName = "Test Name"

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

  val validateInvitationUrl = s"/agent-client-relationships/client/validate-invitation"

  def testValidateInvitationResponseJson(taxService: String, status: String = "Pending"): JsObject = Json.obj(
    "invitationId" -> "AB1234567890",
    "serviceKey" -> taxService,
    "agentName" -> testName,
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
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-response/:uid/:taxService/consent-information" should {

    "redirect to a InsufficientEnrolments exit page when the URL part is not valid for the user enrolments" in :
      authoriseAsClientWithEnrolments("HMRC-MTD-IT")
      await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
      val result = get(routes.DeclineRequestController.show(testUid, "vat").url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController
        .showClient(ClientExitType.CannotFindAuthorisationRequest, Some(RedirectUrl(appConfig.appExternalUrl + routes.DeclineRequestController.show(testUid, "vat").url))).url

    "redirect to NoOutstandingRequests exit page when the invitation data is not found" in {
      authoriseAsClientWithEnrolments("HMRC-MTD-IT")
      await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
      stubPost(validateInvitationUrl, NOT_FOUND, "")
      val result = get(routes.ConsentInformationController.show(testUid, "income-tax").url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.showUnauthorised(ClientExitType.NoOutstandingRequests).url
    }

    "redirect to AgentSuspended exit page when the invitation data is not found" in {
      authoriseAsClientWithEnrolments("HMRC-MTD-IT")
      await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
      stubPost(validateInvitationUrl, FORBIDDEN, "")
      val result = get(routes.ConsentInformationController.show(testUid, "income-tax").url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.showUnauthorised(ClientExitType.AgentSuspended).url
    }

    taxServices.keySet.foreach { taxService =>
      s"Display consent information page for $taxService" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
        stubPost(validateInvitationUrl, OK, testValidateInvitationResponseJson(taxServices(taxService)).toString())
        val result = get(routes.ConsentInformationController.show(testUid, taxService).url)
        result.status shouldBe OK
      }
      s"Redirect correctly to expired exit page for $taxService" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
        stubPost(validateInvitationUrl, OK, testValidateInvitationResponseJson(taxServices(taxService), "Expired").toString())
        val result = get(routes.ConsentInformationController.show(testUid, taxService).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(ClientExitType.AuthorisationRequestExpired).url
      }
      s"Redirect correctly to cancelled exit page for $taxService" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
        stubPost(validateInvitationUrl, OK, testValidateInvitationResponseJson(taxServices(taxService), "Cancelled").toString())
        val result = get(routes.ConsentInformationController.show(testUid, taxService).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(ClientExitType.AuthorisationRequestCancelled).url
      }
      s"Redirect correctly to already accepted exit page for $taxService" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
        stubPost(validateInvitationUrl, OK, testValidateInvitationResponseJson(taxServices(taxService), "Accepted").toString())
        val result = get(routes.ConsentInformationController.show(testUid, taxService).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(ClientExitType.AlreadyAcceptedAuthorisationRequest).url
      }

      s"Redirect correctly to already refused exit page for $taxService" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(ClientJourney(journeyType = "authorisation-response")))
        stubPost(validateInvitationUrl, OK, testValidateInvitationResponseJson(taxServices(taxService), "Rejected").toString())
        val result = get(routes.ConsentInformationController.show(testUid, taxService).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.showClient(ClientExitType.AlreadyRefusedAuthorisationRequest).url
      }
    }
  }
}
