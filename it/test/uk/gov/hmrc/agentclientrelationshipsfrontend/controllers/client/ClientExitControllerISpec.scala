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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType.ClientResponse
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

import java.time.Instant

class ClientExitControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testStatus: InvitationStatus = Accepted
  val testLastModifiedDate: Instant = Instant.now
  val testAgentName = "Agent Name"
  val testTaxService = "income-tax"
  val testEnrolment = "HMRC-MTD-IT"

  val alreadyRespondedStatus: List[InvitationStatus] = List(Accepted, Rejected, DeAuthorised, PartialAuth)

  private def clientJourney(status: Option[InvitationStatus]): ClientJourney = ClientJourney(
    journeyType = ClientResponse, agentName = Some(testAgentName), status = status, lastModifiedDate = Some(testLastModifiedDate)
  )

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  val taxServices: Map[String, String] = Map(
    "income-tax" -> "HMRC-MTD-IT",
    "income-record-viewer" -> "HMRC-PT",
    "vat" -> "HMRC-MTD-VAT",
    "capital-gains-tax-uk-property" -> "HMRC-CGT-PD",
    "plastic-packaging-tax" -> "HMRC-PPT-ORG",
    "country-by-country-reporting" -> "HMRC-CBC-ORG",
    "pillar-2" -> "HMRC-PILLAR2-ORG",
    "trusts-and-estates" -> "HMRC-TERS-ORG",
  )

  taxServices.foreach { case (taxService, taxServiceKey) =>
    s"GET /authorisation-response/exit/agent-suspended/$taxService" should {
      "display the exit page" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(clientJourney(None)))
        val result = get(routes.ClientExitController.show(AgentSuspended, taxService).url)

        println(result.header("Location"))
        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/no-outstanding-requests/$taxService" should {
      "display the exit page" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(clientJourney(None)))
        val result = get(routes.ClientExitController.show(NoOutstandingRequests, taxService).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/cannot-find-authorisation-request/$taxService" should {
      "display the exit page" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(clientJourney(None)))
        val result = get(routes.ClientExitController.show(CannotFindAuthorisationRequest, taxService).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/authorisation-request-cancelled/$taxService" should {
      "display the exit page" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(clientJourney(Some(Cancelled))))
        val result = get(routes.ClientExitController.show(AuthorisationRequestCancelled, taxService).url)

        result.status shouldBe OK
      }
    }

    s"GET /authorisation-response/exit/authorisation-request-expired/$taxService" should {
      "display the exit page" in {
        authoriseAsClientWithEnrolments(taxServices(taxService))
        await(journeyService.saveJourney(clientJourney(Some(Expired))))
        val result = get(routes.ClientExitController.show(AuthorisationRequestExpired, taxService).url)

        result.status shouldBe OK
      }
    }

    alreadyRespondedStatus.foreach(status =>
      s"GET /authorisation-response/exit/authorisation-request-already-responded-to/$taxService with a status of $status" should {
        "display the exit page" in {
          authoriseAsClientWithEnrolments(taxServices(taxService))
          await(journeyService.saveJourney(clientJourney(Some(status))))
          val result = get(routes.ClientExitController.show(AlreadyRespondedToAuthorisationRequest, taxService).url)

          result.status shouldBe OK
        }
      })
  }
}