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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.Pending
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AgentClientRelationshipStub, AuthStubs, ComponentSpecHelper}

import java.time.Instant

class CheckYourAnswerControllerISpec extends ComponentSpecHelper with AuthStubs with AgentClientRelationshipStub:

  val journeyService: ClientJourneyService = app.injector.instanceOf[ClientJourneyService]

  val journeyModel: ClientJourney = ClientJourney(
    "authorisation-response",
    consent = Some(true),
    serviceKey = Some("HMRC-MTD-IT"),
    invitationId = Some("invitationId"),
    agentName = Some("ABC Accountants"),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse("2024-12-01T12:00:00Z"))
  )

  val completeJourney: ClientJourney = ClientJourney(
    "authorisation-response",
    journeyComplete = Some("invitationId")
  )

  val errorModel: ClientJourney = ClientJourney(
    "authorisation-response",
    consent = Some(true),
    serviceKey = Some("HMRC-MTD-IT"),
    invitationId = Some("invitationId"),
    agentName = Some("ABC Accountants"),
    status = Some(Pending),
    lastModifiedDate = Some(Instant.parse("2024-12-01T12:00:00Z")),
    backendErrorResponse = Some(true)
  )

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "The show action" should:

    "return status 200 OK" when:

      "a consent answer is retrieved from the session" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        val result = get(routes.CheckYourAnswerController.show.url)
        result.status shouldBe OK

    "redirect to MYTA when" when:

      "a consent answer is not present in the session" in:
        authoriseAsClient()
        val result = get(routes.CheckYourAnswerController.show.url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe "/agent-client-relationships/manage-your-tax-agents"

  "The submit action" should:

    "redirect the user to the confirmation controller" when:
      "the session already has a journey completed value" in:
        authoriseAsClient()
        await(journeyService.saveJourney(completeJourney))
        givenAcceptAuthorisation("invitationId", NO_CONTENT)
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show.url

      "the user accepts the invitation and a successful response is received from the backend" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        givenAcceptAuthorisation("invitationId", NO_CONTENT)
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show.url

      "the user rejects the invitation and a successful response is received from the backend" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel.copy(consent = Some(false))))
        givenRejectAuthorisation("invitationId", NO_CONTENT)
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show.url

    "redirect to Processing Your Request" when:
      "the user accepts the invitation and a locked response is received from the backend" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        givenAcceptAuthorisation("invitationId", LOCKED)
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.CheckYourAnswerController.processingYourRequest.url

    "redirect to MYTA" when:

      "a consent answer is not present in the session" in:
        authoriseAsClient()
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe "/agent-client-relationships/manage-your-tax-agents"

    "return status 500 INTERNAL_SERVER_ERROR" when:

      "the user accepts the invitation but an unexpected response is received from the backend" in:
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        givenAcceptAuthorisation("ABC123", INTERNAL_SERVER_ERROR)
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe INTERNAL_SERVER_ERROR

      "the user rejects the invitation but an unexpected response is received from the backend" in :
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel.copy(consent = Some(false))))
        givenRejectAuthorisation("ABC123", INTERNAL_SERVER_ERROR)
        val result = post(routes.CheckYourAnswerController.submit.url)(Map())
        result.status shouldBe INTERNAL_SERVER_ERROR

  "Processing your request action" should:
    "redirect the user to the confirmation controller" when:
      "journey complete flag is found in the cache" in :
        authoriseAsClient()
        await(journeyService.saveJourney(completeJourney))
        givenAcceptAuthorisation("invitationId", NO_CONTENT)
        val result = get(routes.CheckYourAnswerController.processingYourRequest.url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ConfirmationController.show.url

    "show tech difficulties" when:
      "backend response error flag is found in the cache" in:
        authoriseAsClient()
        await(journeyService.saveJourney(errorModel))
        givenAcceptAuthorisation("invitationId", NO_CONTENT)
        val result = get(routes.CheckYourAnswerController.processingYourRequest.url)
        result.status shouldBe INTERNAL_SERVER_ERROR

    "stay on processing your request" when :
      "a valid session for CYA is found in the cache" in :
        authoriseAsClient()
        await(journeyService.saveJourney(journeyModel))
        givenAcceptAuthorisation("invitationId", NO_CONTENT)
        val result = get(routes.CheckYourAnswerController.processingYourRequest.url)
        result.status shouldBe OK

    "redirect to MYTA" when :
      "the journey is missing data" in :
        authoriseAsClient()
        await(journeyService.saveJourney(ClientJourney("authorisation-response")))
        givenAcceptAuthorisation("invitationId", NO_CONTENT)
        val result = get(routes.CheckYourAnswerController.processingYourRequest.url)
        result.status shouldBe SEE_OTHER
        result.header("Location").value shouldBe routes.ManageYourTaxAgentsController.show.url