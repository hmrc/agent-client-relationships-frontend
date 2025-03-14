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

class CreateAuthorisationRequestSpec extends AnyWordSpecLike with Matchers {

  val testClientId = "AB123456C"
  val testService = "HMRC-MTD-IT"
  val testName = "Test Name"
  val testCreateAuthorisationRequest: CreateAuthorisationRequest = CreateAuthorisationRequest(
    clientId = testClientId,
    suppliedClientIdType = "nino",
    clientName = testName,
    service = testService,
    clientType = "personal"
  )
  val testCreateAuthorisationRequestJson: JsObject = Json.obj(
    "clientId" -> testClientId,
    "suppliedClientIdType" -> "nino",
    "clientName" -> testName,
    "service" -> testService,
    "clientType" -> "personal"
  )
  "ClientDetailsResponse format" should {
    "write an object to a json" in {
      Json.toJson(testCreateAuthorisationRequest) shouldBe testCreateAuthorisationRequestJson
    }

    "read an object from json" in {
      Json.fromJson[CreateAuthorisationRequest](testCreateAuthorisationRequestJson) shouldBe JsSuccess(testCreateAuthorisationRequest)
    }

    "fail to read an invalid json" in {
      Json.fromJson[CreateAuthorisationRequest](JsString("invalid")).asOpt shouldBe None
    }
  }

}
