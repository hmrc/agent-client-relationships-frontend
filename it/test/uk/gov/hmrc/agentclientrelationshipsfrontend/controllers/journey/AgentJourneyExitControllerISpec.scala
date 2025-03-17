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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, KnownFactType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, JourneyExitType, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class AgentJourneyExitControllerISpec extends ComponentSpecHelper with AuthStubs {

  private val authorisationRequestJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AuthorisationRequest,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.PostalCode), false, None))
  )
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(
    AgentJourneyType.AgentCancelAuthorisation,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.PostalCode), false, None))
  )

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    JourneyExitType.values.foreach(exitType =>
      s"GET /${j.journeyType.toString}/exit/$exitType" should {
        "display the exit page" in {
          authoriseAsAgent()
          await(journeyService.saveJourney(j))
          val result = get(routes.JourneyExitController.show(j.journeyType, exitType).url)
          result.status shouldBe OK
        }
      }))
}
