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

import org.jsoup.Jsoup
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.DoYouAlreadyManageFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.*

class DoYouAlreadyManageControllerISpec extends ComponentSpecHelper with ScalaFutures with AuthStubs:

  val testNino: String = "AB123456C"
  val testCgtRef: String = "XMCGTP123456789"
  val testPostcode: String = "AA11AA"

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  def clientDetailsWithLegacyAgents(isMapped: Boolean) = ClientDetailsResponse(
    name = "Test Name",
    status = None,
    isOverseas = None,
    knownFacts = Seq(testPostcode),
    knownFactType = Some(KnownFactType.PostalCode),
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None,
    isMapped = Some(isMapped),
    clientsLegacyRelationships = Some(Seq("A12345", "B67890"))
  )

  val clientDetailsWithoutLegacyAgents = ClientDetailsResponse(
    name = "Test Name",
    status = None,
    isOverseas = None,
    knownFacts = Seq(testPostcode),
    knownFactType = Some(KnownFactType.PostalCode),
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None,
    isMapped = Some(false),
    clientsLegacyRelationships = Some(Nil)
  )

  val testJourney = AgentJourney(
    journeyType = AgentJourneyType.AuthorisationRequest,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some(testNino),
    clientDetailsResponse = Some(clientDetailsWithLegacyAgents(isMapped = false)),
    knownFact = Some(testPostcode),
    clientConfirmed = Some(true),
    agentType = Some("HMRC-MTD-IT")
  )

  "GET /authorisation-request/already-manage" should :
    "redirect to enter client id when the agent shouldn't be on this page (no client details)" in :
      authoriseAsAgent()
      journeyService.saveJourney(AgentJourney(
        journeyType = AgentJourneyType.AuthorisationRequest,
        clientType = Some("personal"),
        clientService = Some("HMRC-MTD-IT")
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    "redirect to enter client id when the agent shouldn't be on this page (not main agent)" in :
      authoriseAsAgent()
      journeyService.saveJourney(AgentJourney(
        journeyType = AgentJourneyType.AuthorisationRequest,
        clientType = Some("personal"),
        clientService = Some("HMRC-MTD-IT"),
        clientId = Some(testNino),
        clientDetailsResponse = Some(clientDetailsWithLegacyAgents(isMapped = false)),
        knownFact = Some(testPostcode),
        clientConfirmed = Some(true),
        agentType = Some("HMRC-MTD-IT-SUPP")
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    "redirect to enter client id when the agent shouldn't be on this page (has mapping already)" in :
      authoriseAsAgent()
      journeyService.saveJourney(AgentJourney(
        journeyType = AgentJourneyType.AuthorisationRequest,
        clientType = Some("personal"),
        clientService = Some("HMRC-MTD-IT"),
        clientId = Some(testNino),
        clientDetailsResponse = Some(clientDetailsWithLegacyAgents(isMapped = true)),
        knownFact = Some(testPostcode),
        clientConfirmed = Some(true),
        agentType = Some("HMRC-MTD-IT")
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    "redirect to enter client id when the agent shouldn't be on this page (client has no legacy agents)" in :
      authoriseAsAgent()
      journeyService.saveJourney(AgentJourney(
        journeyType = AgentJourneyType.AuthorisationRequest,
        clientType = Some("personal"),
        clientService = Some("HMRC-MTD-IT"),
        clientId = Some(testNino),
        clientDetailsResponse = Some(clientDetailsWithoutLegacyAgents),
        knownFact = Some(testPostcode),
        clientConfirmed = Some(true),
        agentType = Some("HMRC-MTD-IT")
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    "display the do you already manage page" in :
      authoriseAsAgent()
      journeyService.saveJourney(AgentJourney(
        journeyType = AgentJourneyType.AuthorisationRequest,
        clientType = Some("personal"),
        clientService = Some("HMRC-MTD-IT"),
        clientId = Some(testNino),
        clientDetailsResponse = Some(clientDetailsWithLegacyAgents(isMapped = false)),
        knownFact = Some(testPostcode),
        clientConfirmed = Some(true),
        agentType = Some("HMRC-MTD-IT")
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    "display the do you already manage page with prepopulated previous answer" in :
      authoriseAsAgent()
      journeyService.saveJourney(testJourney.copy(
        alreadyManageAuth = Some(true)
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
      val document = Jsoup.parse(result.body)
      document.select("input[value=true]").hasAttr("checked") shouldBe true
      document.select("input[value=false]").hasAttr("checked") shouldBe false

  "POST /authorisation-request/already-manage" should :
    "redirect to enter client id when the agent shouldn't be on this page" in :
      authoriseAsAgent()
      journeyService.saveJourney(AgentJourney(
        journeyType = AgentJourneyType.AuthorisationRequest,
        clientType = Some("personal"),
        clientService = Some("HMRC-MTD-IT")
      )).futureValue
      val result = post(routes.DoYouAlreadyManageController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        DoYouAlreadyManageFieldName -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    "redirect to mapping journey when the agent already manages the client's affairs" in :
      authoriseAsAgent()
      stubPost("/start-auth-mapping-journey", CREATED, Json.obj("redirectUrl" -> "/mapping-journey?id=1234567890").toString)
      journeyService.saveJourney(testJourney).futureValue
      val result = post(routes.DoYouAlreadyManageController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        DoYouAlreadyManageFieldName -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "/mapping-journey?id=1234567890"
    "redirect to CYA when the agent does not already manage the client's affairs" in :
      authoriseAsAgent()
      journeyService.saveJourney(testJourney).futureValue
      val result = post(routes.DoYouAlreadyManageController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        DoYouAlreadyManageFieldName -> Seq("false")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url
    "show error when no option is selected" in :
      authoriseAsAgent()
      journeyService.saveJourney(testJourney).futureValue
      val result = post(routes.DoYouAlreadyManageController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map.empty)
      result.status shouldBe BAD_REQUEST
      val document = Jsoup.parse(result.body)
      document.select(".govuk-error-summary__list > li > a").text() should include("Select ‘yes’ if you already manage Self Assessment for Test Name")

  "GET /authorisation-request/already-manage/cancel-mapping" should :
    "redirect to enter client id when the agent doesn't have a prior answer for 'alreadyManageAuth'" in :
      authoriseAsAgent()
      journeyService.saveJourney(testJourney).futureValue
      val result = get(routes.DoYouAlreadyManageController.cancelMapping(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    "redirect to CYA and set 'abortMapping'" in :
      authoriseAsAgent()
      journeyService.saveJourney(testJourney.copy(
        alreadyManageAuth = Some(true)
      )).futureValue
      val result = get(routes.DoYouAlreadyManageController.cancelMapping(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(AgentJourneyType.AuthorisationRequest).url
      journeyService.getJourney.futureValue.get.alreadyManageAuth shouldBe Some(true)
      journeyService.getJourney.futureValue.get.abortMapping shouldBe Some(true)