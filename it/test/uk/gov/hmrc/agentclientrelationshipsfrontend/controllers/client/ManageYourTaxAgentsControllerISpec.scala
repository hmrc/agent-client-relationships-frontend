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

import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.test.Helpers.LOCATION
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{AgentData, AgentsAuthorisationsResponse, AgentsInvitationsResponse, AuthorisationEventsResponse, ManageYourTaxAgentsData, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes as authRoutes

import java.time.{Instant, LocalDate}

class ManageYourTaxAgentsControllerISpec extends ComponentSpecHelper with AuthStubs:

  val mytaData: ManageYourTaxAgentsData = ManageYourTaxAgentsData(
    agentsInvitations = AgentsInvitationsResponse(agentsInvitations = Seq(
      AgentData(
        uid = "NBM9TUDA",
        agentName = "Test Agent",
        invitations = Seq(
          Invitation(
            invitationId = "1234567890",
            service = "HMRC-MTD-IT",
            clientName = "Some test",
            status = Pending,
            expiryDate = LocalDate.now().plusDays(6),
            lastUpdated = Instant.now()
          ),
          Invitation(
            invitationId = "123LD67891",
            service = "HMRC-PILLAR2-ORG",
            clientName = "Some test",
            status = Pending,
            expiryDate = LocalDate.now().plusDays(15),
            lastUpdated = Instant.now()
          )
        )
      ),
      AgentData(
        uid = "1234590",
        agentName = "Test VAT Agent Ltd",
        invitations = Seq(
          Invitation(
            invitationId = "123456BFX90",
            service = "HMRC-MTD-VAT",
            clientName = "Some test",
            status = Pending,
            expiryDate = LocalDate.now().plusDays(11),
            lastUpdated = Instant.now()
          )
        )
      )
    )),
    agentsAuthorisations = AgentsAuthorisationsResponse(agentsAuthorisations = Seq.empty),
    authorisationEvents = AuthorisationEventsResponse(authorisationEvents = Seq.empty)
  )

  "The show action" should:

    "return status 200 OK" when:

      "signed in as a client" in:
        authoriseAsClient()
        stubGet("/agent-client-relationships/client/authorisations-relationships", OK, Json.toJson(mytaData).toString)
        val result = get(routes.ManageYourTaxAgentsController.show.url)
        result.status shouldBe OK

    "redirect to cannot view page" when:

      "signed in as an agent" in:
        authoriseAsAgent()
        val result = get(routes.ManageYourTaxAgentsController.show.url)
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(authRoutes.AuthorisationController.cannotViewRequest.url)
