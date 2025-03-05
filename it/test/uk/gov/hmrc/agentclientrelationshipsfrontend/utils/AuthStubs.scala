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

package uk.gov.hmrc.agentclientrelationshipsfrontend.utils

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import play.api.test.Helpers.OK
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubPost

trait AuthStubs {

  val testArn = "ARN123456"

  def authoriseAsAgent(arn: String = testArn): StubMapping = stubPost(
    "/auth/authorise",
    OK,
    Json.obj(
      "authorisedEnrolments" -> Json.arr(Json.obj(
        "key" -> "HMRC-AS-AGENT",
        "identifiers" -> Json.arr(Json.obj(
          "key" -> "AgentReferenceNumber",
          "value" -> arn
        ))
      )),
      "affinityGroup" -> "Agent",
      "confidenceLevel" -> 50,
      "allEnrolments" -> Json.arr()
    ).toString
  )

  def authoriseAsClient(): StubMapping = stubPost(
    "/auth/authorise",
    OK,
    Json.obj(
      "affinityGroup" -> "Individual",
      "confidenceLevel" -> 250,
      "allEnrolments" -> Json.arr()
    ).toString
  )
  def authoriseAsClientWithEnrolments(enrolment: String, affinityGroup: String = "Individual"): StubMapping = stubPost(
    "/auth/authorise",
    OK,
    Json.obj(
      "authorisedEnrolments" -> Json.arr(Json.obj(
        "key" -> enrolment,
        "identifiers" -> Json.arr(Json.obj(
          "key" -> "AnyIdentifier",
          "value" -> "AnyValue"
        ))
      )),
      "affinityGroup" -> affinityGroup,
      "confidenceLevel" -> 250,
      "allEnrolments" -> Json.arr(Json.obj(
        "key" -> enrolment,
        "identifiers" -> Json.arr(Json.obj(
          "key" -> "AnyIdentifier",
          "value" -> "AnyValue"
        ))
      ))
    ).toString
  )

}