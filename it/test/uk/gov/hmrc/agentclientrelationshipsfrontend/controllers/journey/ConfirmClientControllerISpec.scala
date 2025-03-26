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

import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, ClientStatus, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class ConfirmClientControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testNino: String = "AB123456C"
  val testCgtRef: String = "XMCGTP123456789"
  val testPostcode: String = "AA11AA"

  def noAuthJourney(journeyType: AgentJourneyType): AgentJourney = AgentJourney(
    journeyType,
    Some("personal"),
    Some("HMRC-CGT-PD"),
    Some(testCgtRef),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  def noAuthJourneyWithSupportedRoles(journeyType: AgentJourneyType): AgentJourney = AgentJourney(
    journeyType,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  def alreadyAuthJourney(journeyType: AgentJourneyType): AgentJourney = AgentJourney(
    journeyType,
    Some("personal"),
    Some("HMRC-CGT-PD"),
    Some(testCgtRef),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, Some("HMRC-CGT-PD"))),
    Some(testPostcode)
  )

  def existingPendingRequestJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    Some("personal"),
    Some("HMRC-CGT-PD"),
    Some(testCgtRef),
    Some(ClientDetailsResponse("Test Name", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), true, None)),
    Some(testPostcode)
  )

  def clientInsolventJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    Some("personal"),
    Some("HMRC-CGT-PD"),
    Some(testCgtRef),
    Some(ClientDetailsResponse("Test Name", Some(ClientStatus.Insolvent), None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  def clientStatusInvalidJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    Some("personal"),
    Some("HMRC-CGT-PD"),
    Some(testCgtRef),
    Some(ClientDetailsResponse("Test Name", Some(ClientStatus.Deregistered), None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None)),
    Some(testPostcode)
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/confirm-client" should {
    "redirect to enter client id when it is missing" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"), clientService = Some("HMRC-MTD-IT"))))
      val result = get(routes.ConfirmClientController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    }
    List(noAuthJourney(AgentJourneyType.AuthorisationRequest), noAuthJourney(AgentJourneyType.AgentCancelAuthorisation))
      .foreach(j => s"display the confirm client page on ${j.journeyType.toString} journey" in {
        authoriseAsAgent()
        await(journeyService.saveJourney(j))
        val result = get(routes.ConfirmClientController.show(j.journeyType).url)
        result.status shouldBe OK
      })
  }

  "POST /authorisation-request/confirm-client" should {
    "redirect to select agent role page when service has supported roles and client is confirmed" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourneyWithSupportedRoles(AgentJourneyType.AuthorisationRequest)))
      val result = post(routes.ConfirmClientController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectAgentRoleController.show(AgentJourneyType.AuthorisationRequest).url
    }
    "redirect to check your answers after confirming client on authorisation-request journey" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(AgentJourneyType.AuthorisationRequest)))
      val result = post(routes.ConfirmClientController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url
    }

    "redirect to check your answers page when confirming client to deAuth with existing authorisation" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(alreadyAuthJourney(AgentJourneyType.AgentCancelAuthorisation)))
      val result = post(routes.ConfirmClientController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(AgentJourneyType.AgentCancelAuthorisation).url
    }

    "redirect to start again when not confirming client" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(AgentJourneyType.AgentCancelAuthorisation)))
      val result = post(routes.ConfirmClientController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "confirmClient" -> Seq("false")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.StartJourneyController.startJourney(AgentJourneyType.AgentCancelAuthorisation).url
    }

    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(noAuthJourney(AgentJourneyType.AuthorisationRequest)))
      val result = post(routes.ConfirmClientController.onSubmit(AgentJourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
