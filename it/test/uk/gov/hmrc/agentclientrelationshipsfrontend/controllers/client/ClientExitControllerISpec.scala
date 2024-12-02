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

import play.api.http.Status.OK
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class ClientExitControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testUid = "ABCD"
  val testStatus = "pending"
  val testNormalizedAgentName = "abc_ltd"
  val testLastModifiedDate = "2025-11-11"
  val testTaxService = "income-tax"

    s"GET /authorisation-response/exit/$AgentSuspended" should {
      "display the exit page" in {
        authoriseAsClient()
        val result = get(routes.ClientExitController.show(AgentSuspended, None, None,
        ).url)

        println(routes.ClientExitController.show(AgentSuspended,
          None, None))

        println(s"\n\n$result")

        result.status shouldBe OK
      }
    }
}