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
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.http.HeaderCarrier

class StartControllerISpec extends ComponentSpecHelper with ScalaFutures with AuthStubs {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val testUid = "ABCD"
  val testStatus = "Pending"
  val testNormalizedAgentName = "abc_ltd"
  val testLastModifiedDate = "2017-02-03T11:25:30.00Z"
  val testTaxService = "income-tax"
  val testName = "Test Name"

  val alreadyRespondedStatuses: List[String] = List(
    "accept", "rejected", "deauthorised", "partialauth"
  )

  val validTaxServiceNames: List[String] = List(
    "income-tax",
    "income-record-viewer",
    "vat",
    "capital-gains-tax-uk-property",
    "plastic-packaging-tax",
    "country-by-country-reporting",
    "pillar-2",
    "trusts-and-estates")

  def getValidateLinkResponseUrl(uid: String, normalizedAgentName: String) = s"/agent-client-relationships/agent/agent-reference/uid/$uid/$normalizedAgentName"

  def testValidateLinkResponseJson(status: String): JsObject = Json.obj(
    "name" -> testName,
    "status" -> status,
    "lastModifiedDate" -> testLastModifiedDate
  )

  val serviceConfigurationService: ClientServiceConfigurationService = app.injector.instanceOf[ClientServiceConfigurationService]
  val agentClientRelationshipsConnector: AgentClientRelationshipsConnector = app.injector.instanceOf[AgentClientRelationshipsConnector]

  "GET /appoint-someone-to-deal-with-HMRC-for-you/:uid/:normalizedAgentName/:taxService" should {

    "Return Not Found when tax service name not valid" in {

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, "invalidTaxService").url)
      result.status shouldBe NOT_FOUND
    }

    "Redirect to routes.ClientExitController.show(SERVER_ERROR)" in {

      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), INTERNAL_SERVER_ERROR, testValidateLinkResponseJson(testStatus).toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "routes.ClientExitController.show(SERVER_ERROR)"
    }

    "Redirect to Agent Suspended page if the status is Suspended" in {

      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), FORBIDDEN, testValidateLinkResponseJson(testStatus).toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.show(AgentSuspended, testNormalizedAgentName, "").url
    }

    "Redirect to routes.ClientExitController.show(AGENT_NOT_FOUND) if there are no invitations found" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), NOT_FOUND, testValidateLinkResponseJson(testStatus).toString)
      
      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.show(NoOutstandingRequests, testNormalizedAgentName, "").url
    }
    alreadyRespondedStatuses.foreach { status =>
      s"Redirect to Already Responded to when the invitation status is $status" in {
        stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), OK, testValidateLinkResponseJson(status).toString)
        val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ClientExitController.show(AlreadyRespondedToAuthorisationRequest, testNormalizedAgentName, testLastModifiedDate).url
      }
    }

    "Redirect to routes.ClientExitController.show(NoOutstandingRequests) when the status is Cancelled" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), OK, testValidateLinkResponseJson("cancelled").toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.show(AuthorisationRequestCancelled, testNormalizedAgentName, testLastModifiedDate).url
    }

    "Redirect to routes.ClientExitController.show(AuthorisationRequestExpired) when the status is Expired" in {
      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), OK, testValidateLinkResponseJson("expired").toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ClientExitController.show(AuthorisationRequestExpired, testNormalizedAgentName, testLastModifiedDate).url
    }

    validTaxServiceNames.foreach { taxService =>
      s"Display start page for $taxService when the status is Pending" in {

        stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), OK, testValidateLinkResponseJson("Pending").toString)

        val result = get(routes.StartController.show(testUid, testNormalizedAgentName, taxService).url)
        result.status shouldBe OK
      }

    }
  }
}