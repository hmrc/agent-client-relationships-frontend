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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.client

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation

import java.time.{Instant, LocalDate}

class ManageYourTaxAgentsDataSpec extends AnyWordSpecLike with Matchers:

  val date: LocalDate = LocalDate.now()
  val instant: Instant = Instant.now()
  val json: JsObject = Json.obj(
    "agentsInvitations" -> Json.obj("agentsInvitations" -> Json.arr(Json.obj(
      "uid" -> "testUid",
      "agentName" -> "testAgentName",
      "invitations" -> Json.arr(Json.obj(
        "invitationId" -> "invitationId",
        "service" -> "HMRC-MTD-IT",
        "expiryDate" -> date,
        "clientName" -> "testName",
        "status" -> Pending.toString,
        "lastUpdated" -> instant
      ))
    ))),
    "agentsAuthorisations" -> Json.obj("agentsAuthorisations" -> Json.arr(Json.obj(
      "agentName" -> "testAgentName",
      "arn" -> "testArn",
      "authorisations" -> Json.arr(Json.obj(
        "uid" -> "testUid",
        "service" -> "HMRC-MTD-IT",
        "clientId" -> "testNino",
        "date" -> date,
        "arn" -> "testArn",
        "agentName" -> "testAgentName"
      ))
    ))),
    "authorisationEvents" -> Json.obj("authorisationEvents" -> Json.arr(Json.obj(
      "agentName" -> "testAgentName",
      "service" -> "HMRC-MTD-IT",
      "date" -> date,
      "eventType" -> Pending.toString
    )))
  )
  val model: ManageYourTaxAgentsData = ManageYourTaxAgentsData(
    AgentsInvitationsResponse(Seq(AgentData(
      "testUid",
      "testAgentName",
      Seq(Invitation(
        invitationId = "invitationId",
        service = "HMRC-MTD-IT",
        expiryDate = date,
        clientName = "testName",
        status = Pending,
        lastUpdated = instant
      ))
    ))),
    AgentsAuthorisationsResponse(Seq(AuthorisedAgent(
      "testAgentName",
      "testArn",
      Seq(Authorisation(
        "testUid",
        "HMRC-MTD-IT",
        "testNino",
        date,
        "testArn",
        "testAgentName"
      ))
    ))),
    AuthorisationEventsResponse(Seq(AuthorisationEvent(
      "testAgentName",
      "HMRC-MTD-IT",
      date,
      Pending
    )))
  )

  "ManageYourTaxAgentsData format" should :
    "read from JSON" in :
      json.as[ManageYourTaxAgentsData] shouldBe model

    "write from JSON" in :
      Json.toJson(model) shouldBe json