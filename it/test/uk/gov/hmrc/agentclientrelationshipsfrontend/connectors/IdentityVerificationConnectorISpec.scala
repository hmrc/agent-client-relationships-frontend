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

import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.IvResult
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.IvResult.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.http.HeaderCarrier

class IdentityVerificationConnectorISpec extends ComponentSpecHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val connector: IdentityVerificationConnector = app.injector.instanceOf[IdentityVerificationConnector]

  val testJourneyId = "123"

  def ivUrl(id: String) = s"/mdtp/journey/journeyId/$id"

  "getIVResult" should {
    Seq(Success, Incomplete, PreconditionFailed, LockedOut, InsufficientEvidence, FailedMatching,
      TechnicalIssue, UserAborted, TimedOut, FailedIv, FailedDirectorCheck).foreach { ivResult =>
      s"return the $ivResult reason when the journey id is valid" in {
        stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> Json.toJson[IvResult](ivResult)).toString)
        val result = connector.getIVResult(testJourneyId)
        await(result) shouldBe Some(ivResult)
      }
    }
    "return nothing when the journey id is not valid" in {
      stubGet(ivUrl(testJourneyId), NOT_FOUND, "")
      val result = connector.getIVResult(testJourneyId)
      await(result) shouldBe None
    }
  }
}
