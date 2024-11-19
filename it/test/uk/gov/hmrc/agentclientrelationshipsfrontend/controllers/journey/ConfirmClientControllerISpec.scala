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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyExitType, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, ClientStatus, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class ConfirmClientControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testNino: String = "AB123456C"
  val testPostcode: String = "AA11AA"

  def noAuthJourney(journeyType: JourneyType): Journey = Journey(
    journeyType,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  def alreadyAuthJourney(journeyType: JourneyType): Journey = Journey(
    journeyType,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, Some("HMRC-MTD-IT"))),
    Some(testPostcode)
  )

  def existingPendingRequestJourney: Journey = Journey(
    JourneyType.AuthorisationRequest,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), true, None)),
    Some(testPostcode)
  )

  def clientInsolventJourney: Journey = Journey(
    JourneyType.AuthorisationRequest,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("Test Name", Some(ClientStatus.Insolvent), None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  def clientStatusInvalidJourney: Journey = Journey(
    JourneyType.AuthorisationRequest,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("Test Name", Some(ClientStatus.Deregistered), None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  val journeyService: JourneyService = app.injector.instanceOf[JourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/confirm-client" should {
    "redirect to enter client id when it is missing" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("personal"), clientService = Some("HMRC-MTD-IT"))))
      val result = get(routes.ConfirmClientController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(JourneyType.AuthorisationRequest).url
    }
    List(noAuthJourney(JourneyType.AuthorisationRequest), noAuthJourney(JourneyType.AgentCancelAuthorisation))
      .foreach(j => s"display the confirm client page on ${j.journeyType.toString} journey" in {
        authoriseAsAgent()
        await(journeyService.saveJourney(j))
        val result = get(routes.ConfirmClientController.show(j.journeyType).url)
        result.status shouldBe OK
      })
  }

  "POST /authorisation-request/confirm-client" should {
    "redirect to check your answers after confirming client on authorisation-request journey" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(JourneyType.AuthorisationRequest)))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "routes.CheckYourAnswersController.show(journeyType).url"
    }

    "redirect to authorisation-exists exit page when confirming client with existing authorisation" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(alreadyAuthJourney(JourneyType.AuthorisationRequest)))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(JourneyType.AuthorisationRequest, JourneyExitType.AuthorisationAlreadyExists).url
    }

    "redirect to check your answers page when confirming client to deAuth with existing authorisation" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(alreadyAuthJourney(JourneyType.AgentCancelAuthorisation)))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "routes.CheckYourAnswersController.show(journeyType).url"
    }

    "redirect to not-authorised exist page when confirming client to deAuth with no existing authorisation" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(JourneyType.AgentCancelAuthorisation)))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(JourneyType.AgentCancelAuthorisation, JourneyExitType.NoAuthorisationExists).url
    }

    "redirect to client-insolvent page when client is insolvent" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(clientInsolventJourney))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(JourneyType.AuthorisationRequest, JourneyExitType.ClientStatusInsolvent).url
    }

    "redirect to client-status-invalid page when client status is invalid" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(clientStatusInvalidJourney))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(JourneyType.AuthorisationRequest, JourneyExitType.ClientStatusInvalid).url
    }

    "redirect to start again when not confirming client" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(JourneyType.AgentCancelAuthorisation)))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmClient" -> Seq("false")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.StartJourneyController.startJourney(JourneyType.AgentCancelAuthorisation).url
    }

    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(JourneyType.AuthorisationRequest)))
      val result = post(routes.ConfirmClientController.onSubmit(JourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
