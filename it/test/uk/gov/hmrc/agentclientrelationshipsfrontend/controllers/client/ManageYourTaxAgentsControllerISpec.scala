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
import play.api.test.Helpers.{LOCATION, await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{AgentData, AgentsAuthorisationsResponse, AgentsInvitationsResponse, Authorisation, AuthorisationEventsResponse, AuthorisationsCache, ManageYourTaxAgentsData, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.{stubGet, stubPost}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.routes as authRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AuthorisationsCacheService
import uk.gov.hmrc.mongo.cache.DataKey

import java.time.{Instant, LocalDate}

class ManageYourTaxAgentsControllerISpec extends ComponentSpecHelper with AuthStubs with AgentClientRelationshipStub:

  val authorisedAgentsCacheService: AuthorisationsCacheService = app.injector.instanceOf[AuthorisationsCacheService]
  val authArn: String = "TARN0000001"
  val testAuthorisationCacheId: String = "auth001"
  val invalidAuthorisationCacheId: String = "notFound"
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
  val testAuthorisation: Authorisation = Authorisation(
    uid = testAuthorisationCacheId,
    service = "HMRC-MTD-IT",
    clientId = "ABCDEF123456789",
    date = LocalDate.now().minusDays(10),
    arn = authArn,
    agentName = "ABC Accountants",
    deauthorised = None
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
        val url = routes.ManageYourTaxAgentsController.show.url
        val result = get(url)
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(authRoutes.AuthorisationController.cannotViewRequest(None, None).url)

  "The show confirm deauth action" should:

    "return status 200" when:

      "authorisation id is valid" in:
        authoriseAsClient()
        await(authorisedAgentsCacheService.put[AuthorisationsCache](
          DataKey("authorisationsCache"),
          AuthorisationsCache(authorisations = Seq(testAuthorisation))))
        val result = get(routes.ManageYourTaxAgentsController.showConfirmDeauth(testAuthorisationCacheId).url)
        result.status shouldBe OK

    "redirect to manage your tax agents page" when:

      "authorisation id is valid but has already been deauthorised" in:
        authoriseAsClient()
        await(authorisedAgentsCacheService.put[AuthorisationsCache](
          DataKey("authorisationsCache"),
          AuthorisationsCache(authorisations = Seq(
            testAuthorisation.copy(
              deauthorised = Some(true)
            )
          ))))
        val result = get(routes.ManageYourTaxAgentsController.showConfirmDeauth(testAuthorisationCacheId).url)
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(routes.ManageYourTaxAgentsController.show.url)

    "return status 404" when:

      "authorisation id is invalid" in:
        authoriseAsClient()
        val result = get(routes.ManageYourTaxAgentsController.showConfirmDeauth(invalidAuthorisationCacheId).url)
        result.status shouldBe NOT_FOUND

  "The submit confirm deauth action" should:

    "return status 400 BAD_REQUEST" when:

      "form is empty" in:
        authoriseAsClient()
        await(authorisedAgentsCacheService.put[AuthorisationsCache](
          DataKey("authorisationsCache"),
          AuthorisationsCache(authorisations = Seq(testAuthorisation))))
        val result = post(routes.ManageYourTaxAgentsController.submitDeauth(testAuthorisationCacheId).url)(Map.empty)
        result.status shouldBe BAD_REQUEST

    "redirect to manage your tax agents page" when:

      "authorisation id is valid but has already been deauthorised" in:
        authoriseAsClient()
        await(authorisedAgentsCacheService.put[AuthorisationsCache](
          DataKey("authorisationsCache"),
          AuthorisationsCache(authorisations = Seq(
            testAuthorisation.copy(
              deauthorised = Some(true)
            )
          ))))
        val result = post(routes.ManageYourTaxAgentsController.submitDeauth(testAuthorisationCacheId).url)(Map("clientConfirmDeauth" -> Seq("true")))
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(routes.ManageYourTaxAgentsController.show.url)

      "authorisation id is invalid" in:
        authoriseAsClient()
        val result = post(routes.ManageYourTaxAgentsController.submitDeauth(invalidAuthorisationCacheId).url)(Map("clientConfirmDeauth" -> Seq("true")))
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(routes.ManageYourTaxAgentsController.show.url)

      "authorisation id is valid and user selects No" in:
        authoriseAsClient()
        await(authorisedAgentsCacheService.put[AuthorisationsCache](
          DataKey("authorisationsCache"),
          AuthorisationsCache(authorisations = Seq(testAuthorisation))))
        val result = post(routes.ManageYourTaxAgentsController.submitDeauth(testAuthorisationCacheId).url)(Map("clientConfirmDeauth" -> Seq("false")))
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(routes.ManageYourTaxAgentsController.show.url)

    "redirect to deauth complete page" when:

      "authorisation id is valid and user confirms deauth" in:
        authoriseAsClient()
        await(authorisedAgentsCacheService.put[AuthorisationsCache](
          DataKey("authorisationsCache"),
          AuthorisationsCache(authorisations = Seq(testAuthorisation))))
        stubPost(
          s"/agent-client-relationships/agent/$authArn/remove-authorisation",
          NO_CONTENT,
          Json.obj("clientId" -> testAuthorisation.clientId, "service" -> testAuthorisation.service).toString
        )
        val result = post(routes.ManageYourTaxAgentsController.submitDeauth(testAuthorisationCacheId).url)(Map("clientConfirmDeauth" -> Seq("true")))
        result.status shouldBe SEE_OTHER
        result.header(LOCATION) shouldBe Some(routes.ManageYourTaxAgentsController.deauthComplete(testAuthorisationCacheId).url)