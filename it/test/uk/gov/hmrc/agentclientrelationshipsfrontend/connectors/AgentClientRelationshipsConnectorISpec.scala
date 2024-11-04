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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, ClientStatus, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.http.{HeaderCarrier, JsValidationException, UpstreamErrorResponse}

class AgentClientRelationshipsConnectorISpec extends ComponentSpecHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val testConnector: AgentClientRelationshipsConnector = app.injector.instanceOf[AgentClientRelationshipsConnector]

  def getClientDetailsUrl(service: String, clientId: String) = s"/agent-client-relationships/client/$service/details/$clientId"

  val testClientId = "clientId"
  val testService = "HMRC-MTD-IT"
  val testName = "Test Name"
  val testPostCode = "AA1 1AA"
  val testClientDetailsResponse: ClientDetailsResponse = ClientDetailsResponse(
    testName,
    Some(ClientStatus.Insolvent),
    isOverseas = Some(false),
    knownFacts = Seq(testPostCode),
    knownFactType = Some(KnownFactType.PostalCode)
  )
  val testClientDetailsResponseJson: JsObject = Json.obj(
    "name" -> testName,
    "status" -> "Insolvent",
    "isOverseas" -> false,
    "knownFacts" -> Json.arr(testPostCode),
    "knownFactType" -> "PostalCode"
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
}
