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

package uk.gov.hmrc.agentclientrelationshipsfrontend.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.IVResult
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.WireMockSupport

trait IVStubs {
  me: WireMockSupport =>

    def givenIVFailureReasonResponse(failureReason: IVResult): StubMapping = {
      stubFor(
        get(urlEqualTo(s"/mdtp/journey/journeyId/valid-uuid"))
          .willReturn(aResponse()
          .withStatus(200)
          .withBody(s"""{"token":"fd53ef15-5073-401f-8390-ee7b8769452f","result":"${failureReason.value}"}
                      |""".stripMargin))
      )
    }

  def givenIVResponseInvalidUUID(): StubMapping = {
    stubFor(
      get(urlEqualTo(s"/mdtp/journey/journeyId/invalid"))
        .willReturn(aResponse()
        .withStatus(404))
    )
  }

  def givenIVUpsertSucceeded: StubMapping = {
    stubFor(
      put(urlPathMatching("/identity-verification/nino/.+"))
        .willReturn(aResponse()
                      .withStatus(200)))
  }

  def givenIVUpsertFailed: StubMapping = {
    stubFor(
      put(urlPathMatching(s"/identity-verification/nino/.+"))
        .willReturn(aResponse()
                      .withStatus(500)))
  }

}
