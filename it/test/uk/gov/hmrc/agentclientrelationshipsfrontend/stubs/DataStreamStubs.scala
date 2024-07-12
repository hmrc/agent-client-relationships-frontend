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
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.libs.json.Json
import uk.gov.hmrc.agentclientrelationshipsfrontend.audit.AgentInvitationEvent.AgentInvitationEvent
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.WireMockSupport

trait DataStreamStubs extends Eventually {
  me: WireMockSupport =>

  override implicit val patienceConfig = PatienceConfig(scaled(Span(5, Seconds)), scaled(Span(500, Millis)))

  def verifyAuditRequestSent(
    count: Int,
    event: AgentInvitationEvent,
    tags: Map[String, String] = Map.empty,
    detail: Map[String, String] = Map.empty): Unit =
    eventually {
      verify(
        1,
        postRequestedFor(urlPathEqualTo(auditUrl))
          .withRequestBody(
            similarToJson(
              s"""{
              |  "auditSource": "agent-invitations-frontend",
              |  "auditType": "$event",
              |  "tags": ${Json.toJson(tags)},
              |  "detail": ${Json.toJson(detail)}
              |}"""
            ))
      )
    }

  def verifyAuditRequestNotSent(event: AgentInvitationEvent): Unit =
    eventually {
      verify(
        0,
        postRequestedFor(urlPathEqualTo(auditUrl))
          .withRequestBody(
            similarToJson(
              s"""{
              |  "auditSource": "agent-invitations-frontend",
              |  "auditType": "$event"
              |}"""
            ))
      )
    }

  def givenAuditConnector(): Seq[StubMapping] = {
    List(
      stubFor(post(urlPathEqualTo(auditUrl + "/merged")).willReturn(aResponse().withStatus(204))),
      stubFor(post(urlPathEqualTo(auditUrl)).willReturn(aResponse().withStatus(204)))
    )
  }

  private def auditUrl = "/write/audit"

  private def similarToJson(value: String) = equalToJson(value.stripMargin, true, true)

}
