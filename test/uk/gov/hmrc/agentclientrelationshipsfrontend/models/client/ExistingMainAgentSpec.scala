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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.client

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

class ExistingMainAgentSpec extends AnyWordSpecLike with Matchers:

  "ExistingMainAgent" should:

    val json = Json.obj("agencyName" -> "ABC Accountants", "sameAgent" -> true)
    val model = ExistingMainAgent("ABC Accountants", true)

    "read from JSON" in:
      json.as[ExistingMainAgent] shouldBe model

    "write from JSON" in:
      Json.toJson(model) shouldBe json
