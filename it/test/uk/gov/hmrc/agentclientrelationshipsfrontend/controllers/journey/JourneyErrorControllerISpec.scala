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
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyErrorType, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}

class JourneyErrorControllerISpec extends ComponentSpecHelper with AuthStubs {

  private val authorisationRequestJourney: Journey = Journey(
    JourneyType.AuthorisationRequest,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.PostalCode), false, None))
  )
  private val agentCancelAuthorisationJourney: Journey = Journey(
    JourneyType.AgentCancelAuthorisation,
    clientType = Some("personal"),
    clientService = Some("HMRC-MTD-IT"),
    clientId = Some("AB123"),
    clientDetailsResponse = Some(ClientDetailsResponse("Test Name", None, None, Seq("AA11AA"), Some(KnownFactType.PostalCode), false, None))
  )

  val journeyService: JourneyService = app.injector.instanceOf[JourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  List(authorisationRequestJourney, agentCancelAuthorisationJourney).foreach(j =>
    JourneyErrorType.values.foreach(errorCode =>
      s"GET /${j.journeyType.toString}/exit/$errorCode" should {
        "display the exit page" in {
          authoriseAsAgent()
          await(journeyService.saveJourney(j))
          val result = get(routes.JourneyErrorController.show(j.journeyType, errorCode).url)
          result.status shouldBe OK
        }
      }))
}
