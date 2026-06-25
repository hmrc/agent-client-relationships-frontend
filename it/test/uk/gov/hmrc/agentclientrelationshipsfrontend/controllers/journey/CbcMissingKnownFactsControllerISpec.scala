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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class CbcMissingKnownFactsControllerISpec extends ComponentSpecHelper with AuthStubs:

  private val journey: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    clientType = Some("business"),
    clientService = Some("HMRC-CBC-ORG"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.Email), false, None, isMissingEacdKnownFacts = Some(true)))
  )
  private val invalidJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    clientType = Some("business"),
    clientService = Some("HMRC-CBC-ORG"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.Email), false, None, isMissingEacdKnownFacts = Some(false)))
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }


  s"GET /${journey.journeyType.toString}/problem-with-service" should :
    "display the problem page" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = get(routes.CbcMissingKnownFactsController.show(journey.journeyType).url)
      result.status shouldBe OK
      result.body.contains("We cannot find any clients that match this CBC ID") shouldBe true


    "redirect to ASA home when the journey is not valid for this page" in :
      authoriseAsAgent()
      await(journeyService.saveJourney(invalidJourney))
      val result = get(routes.CbcMissingKnownFactsController.show(journey.journeyType).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(journey.journeyType).url
