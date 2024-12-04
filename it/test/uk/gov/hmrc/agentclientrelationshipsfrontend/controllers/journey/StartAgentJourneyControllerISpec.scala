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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class StartAgentJourneyControllerISpec extends ComponentSpecHelper with AuthStubs {

  "GET /authorisation-request/" should {
    "redirect to the select client type page" in {
      authoriseAsAgent()
      val result = get(routes.StartJourneyController.startJourney(JourneyType.AuthorisationRequest).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AuthorisationRequest).url
    }
  }

  "GET /agent-cancel-authorisation/" should {
    "redirect to the select client type page" in {
      authoriseAsAgent()
      val result = get(routes.StartJourneyController.startJourney(JourneyType.AgentCancelAuthorisation).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe routes.SelectClientTypeController.show(JourneyType.AgentCancelAuthorisation).url
    }
  }
}
