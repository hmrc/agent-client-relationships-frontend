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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.ClientResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.Instant

class ClientExitControllerISpec extends ComponentSpecHelper with AuthStubs {

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  val alreadyRespondedStatus: List[InvitationStatus] = List(Accepted, Rejected, DeAuthorised, PartialAuth)

  def journeyModel(status: Option[InvitationStatus]): ClientJourney = ClientJourney(
    ClientResponse,
    agentName = Some("ABC Accountants"),
    status = status,
    lastModifiedDate = Some(Instant.parse("2024-12-01T12:00:00Z"))
  )

    s"GET /authorisation-response/exit/agent-suspended" should {
      "display the exit page" in {
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showUnauthorised(AgentSuspended).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/no-outstanding-requests" should {
      "display the exit page" in {
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showUnauthorised(NoOutstandingRequests).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/cannot-find-authorisation-request" should {
      "display the exit page" in {
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel(None)))
        val result = get(routes.ClientExitController.showClient(CannotFindAuthorisationRequest).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/authorisation-request-cancelled" should {
      "display the exit page" in {
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel(Some(Cancelled))))
        val result = get(routes.ClientExitController.showClient(AuthorisationRequestCancelled).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/authorisation-request-expired" should {
      "display the exit page" in {
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel(Some(Expired))))
        val result = get(routes.ClientExitController.showClient(AuthorisationRequestExpired).url)

        result.status shouldBe OK
      }
    }

    alreadyRespondedStatus.foreach(status =>
      s"GET /authorisation-response/exit/authorisation-request-already-responded-to with a status of $status" should {
        "display the exit page" in {
          authoriseAsClient()
          await(journeyService.saveJourney(journeyModel(Some(status))))
          val result = get(routes.ClientExitController.showClient(AlreadyRespondedToAuthorisationRequest).url)

          result.status shouldBe OK
        }
      })
}