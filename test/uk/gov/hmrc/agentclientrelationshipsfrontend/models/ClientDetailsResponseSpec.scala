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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.*

class ClientDetailsResponseSpec extends AnyWordSpecLike with Matchers {

  val testClientId = "clientId"
  val testService = "HMRC-MTD-IT"
  val testName = "Test Name"
  val testPostCode = "AA1 1AA"
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
  "ClientDetailsResponse format" should {
    "write an object to a json" in {
      Json.toJson(testClientDetailsResponse) shouldBe testClientDetailsResponseJson
    }

    "read an object from json" in {
      Json.fromJson[ClientDetailsResponse](testClientDetailsResponseJson) shouldBe JsSuccess(testClientDetailsResponse)
    }

    "fail to read an invalid json" in {
      Json.fromJson[ClientDetailsResponse](JsString("invalid")).asOpt shouldBe None
    }
  }

}
