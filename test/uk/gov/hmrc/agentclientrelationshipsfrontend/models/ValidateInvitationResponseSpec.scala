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
import play.api.libs.json.Json
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientType.personal
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{ExistingMainAgent, Pending}

import java.time.Instant

class ValidateInvitationResponseSpec extends AnyWordSpecLike with Matchers:

  "ValidateInvitationResponse" should:

    val model = ValidateInvitationResponse(
      "ABC123",
      "HMRC-MTD-VAT",
      "Pep Guardiol",
      Pending,
      Instant.parse("2024-12-01T12:00:00Z"),
      Some(ExistingMainAgent("CFG Solutions", true)),
      Some(personal)
    )

    val json = Json.obj(
      "invitationId" -> "ABC123",
      "serviceKey" -> "HMRC-MTD-VAT",
      "agentName" -> "Pep Guardiol",
      "status" -> "Pending",
      "lastModifiedDate" -> "2024-12-01T12:00:00Z",
      "existingMainAgent" -> Json.obj(
        "agencyName" -> "CFG Solutions",
        "sameAgent" -> true
      ),
      "clientType" -> "personal"
    )

    val optModel = model.copy(existingMainAgent = None, clientType = None)
    val optJson = json.-("existingMainAgent").-("clientType")

    "read from JSON" when:

      "all fields are present" in:
        json.as[ValidateInvitationResponse] shouldBe model

      "optional fields are missing" in:
        optJson.as[ValidateInvitationResponse] shouldBe optModel

    "write to JSON" when:

      "all fields are present" in:
        Json.toJson(model) shouldBe json

      "optional fields are missing" in:
        Json.toJson(optModel) shouldBe optJson