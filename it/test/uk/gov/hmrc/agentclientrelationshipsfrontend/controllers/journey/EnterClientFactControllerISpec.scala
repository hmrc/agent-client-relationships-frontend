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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.KnownFactType.PostalCode
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType, JourneyExitType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class EnterClientFactControllerISpec extends ComponentSpecHelper with AuthStubs {

  val testNino: String = "AB123456C"
  val testPostcode: String = "AA11AA"

  def testItsaJourney(journeyType: AgentJourneyType): AgentJourney = AgentJourney(
    journeyType,
    Some("personal"),
    Some("HMRC-MTD-IT"),
    Some(testNino),
    Some(ClientDetailsResponse("", None, None, Seq(testPostcode), Some(KnownFactType.PostalCode), false, None))
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/client-fact" should {
    "redirect to the journey start when previous answers are missing" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AuthorisationRequest, clientType = Some("personal"))))
      val result = get(routes.EnterClientFactController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(AgentJourneyType.AuthorisationRequest).url
    }
    "display the client identifier page for ITSA" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AuthorisationRequest)))
      val result = get(routes.EnterClientFactController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    }
  }

  "POST /authorisation-request/client-fact" should {
    "redirect to the next page after storing answer for ITSA" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AuthorisationRequest)))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "postcode" -> Seq(testPostcode)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmClientController.show(AgentJourneyType.AuthorisationRequest).url
    }
    "redirect to client-not-found when submitting a mismatching KF" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AuthorisationRequest)))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "postcode" -> Seq("ZZ1 1ZZ")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AgentJourneyType.AuthorisationRequest, JourneyExitType.NotFound).url
    }
    "unset existing answers when submitting a new answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AuthorisationRequest).copy(knownFact = Some("ZZ1 1AA"), clientConfirmed = Some(true))))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "postcode" -> Seq(testPostcode)
      ))
      val updatedJourney = await(journeyService.getJourney(request))
      updatedJourney.get.clientConfirmed shouldBe None
    }
    "leave existing answers intact when submitting the same answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AuthorisationRequest).copy(knownFact = Some(testPostcode), clientConfirmed = Some(true))))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "postcode" -> Seq(testPostcode)
      ))
      val updatedJourney = await(journeyService.getJourney(request))
      updatedJourney.get.clientConfirmed shouldBe Some(true)
    }
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AuthorisationRequest)))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

  "GET /agent-cancel-authorisation/client-fact" should {
    "redirect to the journey start when previous answers are missing" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(AgentJourney(journeyType = AgentJourneyType.AgentCancelAuthorisation, clientType = None)))
      val result = get(routes.EnterClientFactController.show(AgentJourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(AgentJourneyType.AgentCancelAuthorisation).url
    }
    "display the the client identifier page for ITSA" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AgentCancelAuthorisation)))
      val result = get(routes.EnterClientFactController.show(AgentJourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe OK
    }
  }

  "POST /agent-cancel-authorisation/client-fact" should {
    "redirect to the next page after storing answer for ITSA" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AgentCancelAuthorisation)))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(body = Map(
        "postcode" -> Seq(testPostcode)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.ConfirmClientController.show(AgentJourneyType.AgentCancelAuthorisation).url
    }
    "redirect to client-not-found when submitting a mismatching KF" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AgentCancelAuthorisation)))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "postcode" -> Seq("ZZ1 1ZZ")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(AgentJourneyType.AgentCancelAuthorisation, JourneyExitType.NotFound).url
    }
    "unset existing answers when submitting a new answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AgentCancelAuthorisation).copy(knownFact = Some("ZZ1 1AA"), clientConfirmed = Some(true))))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "postcode" -> Seq(testPostcode)
      ))
      val updatedJourney = await(journeyService.getJourney(request))
      updatedJourney.get.clientConfirmed shouldBe None
    }
    "leave existing answers intact when submitting the same answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AgentCancelAuthorisation).copy(knownFact = Some(testPostcode), clientConfirmed = Some(true))))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "postcode" -> Seq(testPostcode)
      ))
      val updatedJourney = await(journeyService.getJourney(request))
      updatedJourney.get.clientConfirmed shouldBe Some(true)
    }
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(testItsaJourney(AgentJourneyType.AgentCancelAuthorisation)))
      val result = post(routes.EnterClientFactController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(body = Map("postcode" -> Seq("")))
      result.status shouldBe BAD_REQUEST
    }
  }

}
