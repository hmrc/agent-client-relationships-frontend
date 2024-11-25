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
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.{AgentSuspended, AlreadyRespondedToAuthorisationRequest, AuthorisationRequestCancelled, AuthorisationRequestExpired, CannotFindAuthorisationRequest, NoOutstandingRequests}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.ClientResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{Accepted, Cancelled, DeAuthorised, Expired, InvitationStatus, PartialAuth, Rejected}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.Instant

class ClientExitControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testStatus: InvitationStatus = Accepted
  val testLastModifiedDate: Instant = Instant.now
  val testAgentName = "Truman"
  val testTaxService = "income-tax"

  val alreadyRespondedStatus: List[InvitationStatus] = List(Accepted, Rejected, DeAuthorised, PartialAuth)

  private def clientJourney(status: Option[InvitationStatus]): ClientJourney = ClientJourney(
    journeyType = ClientResponse, agentName = Some(testAgentName), status = status, lastModifiedDate = Some(testLastModifiedDate)
  )

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  s"GET /authorisation-response/exit/agent-suspended/$testTaxService" should {
    "display the exit page" in {
      authoriseAsClient()
      await(journeyService.saveJourney(clientJourney(None)))
      val result = get(routes.ClientExitController.show(AgentSuspended, testTaxService).url)

      result.status shouldBe OK
    }
  }

  s"GET /authorisation-response/exit/no-outstanding-requests/$testTaxService" should {
    "display the exit page" in {
      authoriseAsClient()
      await(journeyService.saveJourney(clientJourney(None)))
      val result = get(routes.ClientExitController.show(NoOutstandingRequests, testTaxService).url)

      result.status shouldBe OK
    }
  }

  s"GET /authorisation-response/exit/cannot-find-authorisation-request/$testTaxService" should {
    "display the exit page" in {
      authoriseAsClient()
      await(journeyService.saveJourney(clientJourney(None)))
      val result = get(routes.ClientExitController.show(CannotFindAuthorisationRequest, testTaxService).url)

      result.status shouldBe OK
    }
  }

  s"GET /authorisation-response/exit/authorisation-request-cancelled/$testTaxService" should {
    "display the exit page" in {
      authoriseAsClient()
      await(journeyService.saveJourney(clientJourney(Some(Cancelled))))
      val result = get(routes.ClientExitController.show(AuthorisationRequestCancelled, testTaxService).url)

      result.status shouldBe OK
    }
  }

  s"GET /authorisation-response/exit/authorisation-request-expired/$testTaxService" should {
    "display the exit page" in {
      authoriseAsClient()
      await(journeyService.saveJourney(clientJourney(Some(Expired))))
      val result = get(routes.ClientExitController.show(AuthorisationRequestExpired, testTaxService).url)

      result.status shouldBe OK
    }
  }

  alreadyRespondedStatus.foreach(status =>
    s"GET /authorisation-response/exit/authorisation-request-already-responded-to/$testTaxService with a status of $status" should {
      "display the exit page" in {
        authoriseAsClient()
        await(journeyService.saveJourney(clientJourney(Some(status))))
        val result = get(routes.ClientExitController.show(AlreadyRespondedToAuthorisationRequest, testTaxService).url)

        result.status shouldBe OK
      }
    })
}