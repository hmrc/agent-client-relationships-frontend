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

import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType, JourneyExitType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class SelectAgentRoleControllerISpec extends ComponentSpecHelper with ScalaFutures with AuthStubs {

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]
  val journeyType: AgentJourneyType = AgentJourneyType.AuthorisationRequest // this controller is only used on AuthorisationRequest journeys

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  val testPostcode = "AA1 1AA"

  val clientDetailsWithOutExisting: ClientDetailsResponse = ClientDetailsResponse(
    name = "Jay Bridger",
    status = None,
    hasPendingInvitation = false,
    hasExistingRelationshipFor = None,
    knownFactType = Some(KnownFactType.PostalCode),
    knownFacts = Seq(testPostcode),
    isOverseas = Some(false),
    isMapped = Some(false),
    clientsLegacyRelationships = Some(Nil)
  )

  val clientDetailsWithExistingMain: ClientDetailsResponse = clientDetailsWithOutExisting.copy(hasExistingRelationshipFor = Some("HMRC-MTD-IT"))
  val clientDetailsWithExistingSupp: ClientDetailsResponse = clientDetailsWithOutExisting.copy(hasExistingRelationshipFor = Some("HMRC-MTD-IT-SUPP"))

  private val journey = AgentJourney(
    journeyType = journeyType,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    knownFact = Some(testPostcode),
    clientConfirmed = Some(true),
    clientDetailsResponse = Some(clientDetailsWithOutExisting),
    agentType = None
  )

  val agentRoleJourneys: Seq[AgentJourney] = List(
    journey,
    journey.copy(clientDetailsResponse = Some(clientDetailsWithExistingMain)),
    journey.copy(clientDetailsResponse = Some(clientDetailsWithExistingSupp))
  )

  "GET /authorisation-request/agent-role" should {

    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.SelectAgentRoleController.show(journeyType).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to enter client id when no client details are present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(clientDetailsResponse = None)))
      val result = get(routes.SelectAgentRoleController.show(journeyType).url)
      result.header("Location").value shouldBe routes.EnterClientIdController.show(journeyType).url
    }
    agentRoleJourneys.foreach(j => s"display the select agent role when existing relationship is ${j.clientDetailsResponse.get.hasExistingRelationshipFor.getOrElse("non existent")} page" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(j))
      val result = get(routes.SelectAgentRoleController.show(journeyType).url)
      result.status shouldBe OK
    })
  }

  "POST /authorisation-request/agent-role" should {

    "redirect to enter client id when client details are missing" in {
      authoriseAsAgent()
      journeyService.saveJourney(
        journey = AgentJourney(
          journeyType = AgentJourneyType.AuthorisationRequest,
          clientType = Some("personal"),
          clientService = Some("HMRC-MTD-IT")
        )
      ).futureValue
      val result = post(routes.ConfirmClientController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "confirmClient" -> Seq("true")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(AgentJourneyType.AuthorisationRequest).url
    }

    "redirect to Do You Already Manage page when selecting HMRC-MTD-IT for a new relationship when client has an existing legacy agent" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(
        clientDetailsResponse = Some(clientDetailsWithOutExisting.copy(
          isMapped = Some(false),
          clientsLegacyRelationships = Some(Seq("A12345"))
        ))
      )))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq("HMRC-MTD-IT")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.DoYouAlreadyManageController.show(journeyType).url
    }

    "redirect to CYA page (cya will route to kickout) when selecting HMRC-MTD-IT for a new relationship when client has an existing legacy agent with a prior mapping" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(
        clientDetailsResponse = Some(clientDetailsWithOutExisting.copy(
          isMapped = Some(true),
          clientsLegacyRelationships = Some(Seq("A12345"))
        ))
      )))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq("HMRC-MTD-IT")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(journeyType).url
    }

    "redirect to CYA page when selecting HMRC-MTD-IT-SUPP for a new relationship when client has an existing legacy agent" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(
        clientDetailsResponse = Some(clientDetailsWithOutExisting.copy(
          isMapped = Some(false),
          clientsLegacyRelationships = Some(Seq("A12345"))
        ))
      )))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq("HMRC-MTD-IT-SUPP")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(journeyType).url
    }

    List("HMRC-MTD-IT", "HMRC-MTD-IT-SUPP").foreach(role => s"redirect to the CYA page after storing valid answer $role for new relationship" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq(role)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(journeyType).url
    })

    List("HMRC-MTD-IT", "HMRC-MTD-IT-SUPP").foreach(role => s"redirect to the CYA page after storing valid answer $role for change of relationship" in {
      authoriseAsAgent()
      val relationshipToChange = if role == "HMRC-MTD-IT" then clientDetailsWithExistingSupp else clientDetailsWithExistingMain
      await(journeyService.saveJourney(journey.copy(clientDetailsResponse = Some(relationshipToChange))))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq(role)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.CheckYourAnswersController.show(journeyType).url
    })

    "redirect to the 'agent role not changed' page after submitting no change for main role" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(clientDetailsResponse = Some(clientDetailsWithExistingMain))))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq("HMRC-MTD-IT")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(journeyType, JourneyExitType.NoChangeOfAgentRole).url
    }

    "redirect to the agent role not changed page after submitting the no change for supporting role" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(clientDetailsResponse = Some(clientDetailsWithExistingSupp))))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)(Map(
        "agentRole" -> Seq("HMRC-MTD-IT-SUPP")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.JourneyExitController.show(journeyType, JourneyExitType.NoChangeOfAgentRole).url
    }

    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = post(routes.SelectAgentRoleController.onSubmit(journeyType).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
