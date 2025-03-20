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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsObject, Json}

class AgentDetailsSpec extends AnyWordSpecLike with Matchers :

  "AgentDetails" should :

    "read from JSON" when :

      "all fields are present" in :
        val json: JsObject = Json.obj("agencyDetails" -> Json.obj("agencyName" -> "ABC Accountants", "agencyEmail" -> "abc@test.com"))
        val model: AgentDetails = AgentDetails("ABC Accountants", "abc@test.com")

        json.as[AgentDetails] shouldBe model

      "agencyName is not present" in :
        val json: JsObject = Json.obj("agencyDetails" -> Json.obj("agencyEmail" -> "abc@test.com"))
        val model: AgentDetails = AgentDetails("", "abc@test.com")

        json.as[AgentDetails] shouldBe model

      "agencyEmail is not present" in :
        val json: JsObject = Json.obj("agencyDetails" -> Json.obj("agencyName" -> "ABC Accountants"))
        val model: AgentDetails = AgentDetails("ABC Accountants", "")

        json.as[AgentDetails] shouldBe model

      "an expected field is null" in :
        val json: JsObject = Json.obj("agencyDetails" -> Json.obj("agencyEmail" -> "abc@test.com", "agencyName" -> null))
        val model: AgentDetails = AgentDetails("", "abc@test.com")

        json.as[AgentDetails] shouldBe model

      "an expected field is an unexpected type" in :
        val json: JsObject = Json.obj("agencyDetails" -> Json.obj("agencyName" -> true, "agencyEmail" -> "abc@test.com"))
        val model: AgentDetails = AgentDetails("", "abc@test.com")

        json.as[AgentDetails] shouldBe model

      "no fields are present" in :
        val json: JsObject = Json.obj("agencyDetails" -> Json.obj())
        val model: AgentDetails = AgentDetails("", "")

        json.as[AgentDetails] shouldBe model

    "write to JSON" in :
      val model: AgentDetails = AgentDetails("ABC Accountants", "abc@test.com")
      val json: JsObject = Json.obj("agencyName" -> "ABC Accountants", "agencyEmail" -> "abc@test.com")

      Json.toJson(model) shouldBe json
