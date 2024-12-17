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
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{ExistingMainAgent, Pending}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, AuthStubs, ComponentSpecHelper}

import java.time.Instant

class ConfirmConsentControllerISpec extends ComponentSpecHelper with AuthStubs with AgentClientRelationshipStub:

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  val journeyModel: ClientJourney = ClientJourney(
    "authorisation-response",
    consent = Some(true),
    serviceKey = Some("HMRC-MTD-IT"),
    invitationId = Some("ABC123"),
    agentName = Some("ABC Accountants"),
    existingMainAgent = Some(ExistingMainAgent("XYZ Accountants", true)),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse("2024-12-01T12:00:00Z"))
  )
  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "The show action" should:

    "return status 200 OK" when:

      "a service key and agent name are retrieved from the session" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        val result = get(routes.ConfirmConsentController.show.url)
        result.status shouldBe OK

    "return status 400 BAD_REQUEST" when:

      "a service key is not present in the session" in:
        authoriseAsClient()
        val badJourneyModel = journeyModel.copy(serviceKey = None)
        await(journeyService.saveJourney(badJourneyModel))
        val result = get(routes.ConfirmConsentController.show.url)
        result.status shouldBe BAD_REQUEST

      "an agent name is not present in the session" in :
        authoriseAsClient()
        val badJourneyModel = journeyModel.copy(agentName = None)
        await(journeyService.saveJourney(badJourneyModel))
        val result = get(routes.ConfirmConsentController.show.url)
        result.status shouldBe BAD_REQUEST

  "The submit action" should:

    "redirect the user to the Check Your Answer controller" when:

      "the user selects Yes to give consent" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        val result = post(routes.ConfirmConsentController.submit.url)(Map("confirmConsent" -> Seq("true")))
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.CheckYourAnswerController.show.url

      "the user selects No to deny consent" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        val result = post(routes.ConfirmConsentController.submit.url)(Map("confirmConsent" -> Seq("false")))
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.CheckYourAnswerController.show.url

    "return status 400 BAD_REQUEST" when:

      "the user does not answer the consent question" in :
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        val result = post(routes.ConfirmConsentController.submit.url)(Map("confirmConsent" -> Seq("")))
        result.status shouldBe BAD_REQUEST

      "a service key is not present in the session" in:
        authoriseAsClient()
        val badJourneyModel = journeyModel.copy(serviceKey = None)
        await(journeyService.saveJourney(badJourneyModel))
        val result = get(routes.ConfirmConsentController.show.url)
        result.status shouldBe BAD_REQUEST

      "an agent name is not present in the session" in :
        authoriseAsClient()
        val badJourneyModel = journeyModel.copy(agentName = None)
        await(journeyService.saveJourney(badJourneyModel))
        val result = get(routes.ConfirmConsentController.show.url)
        result.status shouldBe BAD_REQUEST
