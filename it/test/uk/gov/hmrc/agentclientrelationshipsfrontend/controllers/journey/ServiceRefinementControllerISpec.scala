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
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyState, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class ServiceRefinementControllerISpec extends ComponentSpecHelper with AuthStubs {

  val journeyService: JourneyService = app.injector.instanceOf[JourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/refine-service" should {
    val journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("trust"), clientService = Some("HMRC-TERS-ORG"))
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.ServiceRefinementController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to the journey start when no service present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(clientService = None)))
      val result = get(routes.ServiceRefinementController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AuthorisationRequest).url
    }
    "display the refine service page" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = get(routes.ServiceRefinementController.show(JourneyType.AuthorisationRequest).url)
      result.status shouldBe OK
    }
  }

  "POST /authorisation-request/refine-service" should {
    val journey = Journey(journeyType = JourneyType.AuthorisationRequest, clientType = Some("trust"), clientService = Some("HMRC-TERS-ORG"))
    "redirect to the next page after storing answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = post(routes.ServiceRefinementController.onSubmit(JourneyType.AuthorisationRequest).url)(Map(
        "clientService" -> Seq("HMRC-TERSNT-ORG")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(JourneyType.AuthorisationRequest).url
    }

    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = post(routes.ServiceRefinementController.onSubmit(JourneyType.AuthorisationRequest).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

  "GET /agent-cancel-authorisation/refine-service" should {
    val journey = Journey(journeyType = JourneyType.AgentCancelAuthorisation, clientType = Some("trust"), clientService = Some("HMRC-TERS-ORG"))
    "redirect to ASA dashboard when no journey session present" in {
      authoriseAsAgent()
      val result = get(routes.ServiceRefinementController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "redirect to the journey start when no service present" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey.copy(clientService = None)))
      val result = get(routes.ServiceRefinementController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AgentCancelAuthorisation).url
    }
    "display the refine service page" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = get(routes.ServiceRefinementController.show(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe OK
    }
  }

  "POST /agent-cancel-authorisation/refine-service" should {
    val journey = Journey(journeyType = JourneyType.AgentCancelAuthorisation, clientType = Some("trust"), clientService = Some("HMRC-TERS-ORG"))
    "redirect to the enter client id page after storing answer" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = post(routes.ServiceRefinementController.onSubmit(JourneyType.AgentCancelAuthorisation).url)(Map(
        "clientService" -> Seq("HMRC-TERS-ORG")
      ))
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.EnterClientIdController.show(JourneyType.AgentCancelAuthorisation).url
    }

    "show an error when no selection is made" in {
      authoriseAsAgent()
      await(journeyService.saveJourney(journey))
      val result = post(routes.ServiceRefinementController.onSubmit(JourneyType.AgentCancelAuthorisation).url)("")
      result.status shouldBe BAD_REQUEST
    }
  }

}
