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
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.WireMockSupport
import uk.gov.hmrc.domain.Nino

trait CitizenDetailsStub {
  me: WireMockSupport =>

  def givenCitizenDetailsAreKnownFor(nino: Nino, firstName: String, lastName: String) =
    stubFor(
      get(urlEqualTo(s"/citizen-details/nino/${nino.value}"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(s"""{
                         |   "name": {
                         |      "current": {
                         |         "firstName": "$firstName",
                         |         "lastName": "$lastName"
                         |      },
                         |      "previous": []
                         |   },
                         |   "ids": {
                         |      "nino": "${nino.value}"
                         |   },
                         |   "dateOfBirth": "11121971"
                         |}""".stripMargin)))

  def givenCitizenDetailsReturns404For(nino: Nino) =
    stubFor(
      get(urlEqualTo(s"/citizen-details/nino/${nino.value}"))
        .willReturn(aResponse()
          .withStatus(404)))

  def givenCitizenDetailsReturns400For(nino: Nino) =
    stubFor(
      get(urlEqualTo(s"/citizen-details/nino/${nino.value}"))
        .willReturn(aResponse()
          .withStatus(400)))
}
