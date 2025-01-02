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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourney, AgentJourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.AgentJourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class SelectClientTypeControllerISpec extends ComponentSpecHelper with AuthStubs {

  private val authorisationRequestJourney: AgentJourney = AgentJourney(AgentJourneyType.AuthorisationRequest)
  private val agentCancelAuthorisationJourney: AgentJourney = AgentJourney(AgentJourneyType.AgentCancelAuthorisation)

  val journeyService: AgentJourneyService = app.injector.instanceOf[AgentJourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/client-type" should {
    "display the select client type page" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(authorisationRequestJourney))
      val result = get(routes.SelectClientTypeController.show(AgentJourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    }
  }

  "POST /authorisation-request/client-type" should {
    Seq("personal", "business", "trust").foreach(clientType => s"redirect to the next page after storing $clientType as the answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(authorisationRequestJourney))
      val result = post(routes.SelectClientTypeController.onSubmit(AgentJourneyType.AuthorisationRequest).url)(Map(
        "clientType" -> Seq(clientType)
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectServiceController.show(AgentJourneyType.AuthorisationRequest).url
    })
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(authorisationRequestJourney))
      val result = post(routes.SelectClientTypeController.onSubmit(AgentJourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

  "GET /agent-cancel-authorisation/client-type" should {
    "display the select client type page" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(agentCancelAuthorisationJourney))
      val result = get(routes.SelectClientTypeController.show(AgentJourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe OK
    }
  }

  "POST /agent-cancel-authorisation/client-type" should {
    Seq("personal", "business", "trust").foreach(clientType => s"redirect to the next page after storing $clientType as the answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(agentCancelAuthorisationJourney))
      val result = post(routes.SelectClientTypeController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)(Map(
        "clientType" -> Seq("personal")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectServiceController.show(AgentJourneyType.AgentCancelAuthorisation).url
    })
    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(agentCancelAuthorisationJourney))
      val result = post(routes.SelectClientTypeController.onSubmit(AgentJourneyType.AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
