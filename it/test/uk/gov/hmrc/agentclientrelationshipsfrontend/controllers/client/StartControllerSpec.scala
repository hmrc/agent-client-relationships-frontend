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

import org.jsoup.Jsoup
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.http.HeaderCarrier

class StartControllerSpec extends ComponentSpecHelper with ScalaFutures with AuthStubs {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val testUid = "ABCD"
  val testNormalizedAgentName = "test-name"
  val testTaxService = "income-tax"
  val testAgentName = "Test Name"

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

  val testValidateLinkResponseJson: JsObject = Json.obj(
    "name" -> testAgentName,
  )

  val serviceConfigurationService: ClientServiceConfigurationService = app.injector.instanceOf[ClientServiceConfigurationService]
  val agentClientRelationshipsConnector: AgentClientRelationshipsConnector = app.injector.instanceOf[AgentClientRelationshipsConnector]

  "GET /appoint-someone-to-deal-with-HMRC-for-you/:uid/:normalizedAgentName/:taxService" should {
    "Returns Not Found when tax service name not valid" in {

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, "invalidTaxService").url)
      result.status shouldBe NOT_FOUND
    }
    
    "Redirect to routes.ClientExitController.show(AGENT_NOT_FOUND)" in {

      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), NOT_FOUND, testValidateLinkResponseJson.toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "routes.ClientExitController.show(AGENT_NOT_FOUND)"
    }

    "Redirect to routes.ClientExitController.show(AGENT_SUSPENDED)" in {

      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), FORBIDDEN, testValidateLinkResponseJson.toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "routes.ClientExitController.show(AGENT_SUSPENDED)"
    }

    "Redirect to routes.ClientExitController.show(SERVER_ERROR)" in {

      stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), INTERNAL_SERVER_ERROR, testValidateLinkResponseJson.toString)

      val result = get(routes.StartController.show(testUid, testNormalizedAgentName, testTaxService).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "routes.ClientExitController.show(SERVER_ERROR)"
    }
    
    validTaxServiceNames.foreach { taxService =>
      s"Display start page for $taxService" in {

        stubGet(getValidateLinkResponseUrl(testUid, testNormalizedAgentName), OK, testValidateLinkResponseJson.toString)

        val result = get(routes.StartController.show(testUid, testNormalizedAgentName, taxService).url)
        result.status shouldBe OK
        val body = Jsoup.parse(result.body)
        body.select("h1").text() should include(testAgentName)
      }

    }
  }
}