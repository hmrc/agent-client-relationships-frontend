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

import org.scalatest.concurrent.Futures.whenReady
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Environment
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.mvc.*
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.actions.{Actions, AuthActions, GetJourneyActionRefiner}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.SelectClientTypeController
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{Journey, JourneyRequest, JourneyState, JourneyType}
import uk.gov.hmrc.agentclientrelationshipsfrontend.repositories.journey.JourneyRepository
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.journey.JourneyService
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.journey.SelectClientTypePage
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.journey.routes

import scala.concurrent.{ExecutionContext, Future}

class SelectClientTypeControllerISpec extends ComponentSpecHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  private val authorisationRequestJourney: Journey = Journey(JourneyType.AuthorisationRequest, journeyState = JourneyState.SelectClientType)
  private val agentCancelAuthorisationJourney: Journey = Journey(JourneyType.AgentCancelAuthorisation, journeyState = JourneyState.SelectClientType)
  val authorisationRequestJourneyRequest: JourneyRequest[?] = new JourneyRequest(journey = authorisationRequestJourney, request)
  val agentCancelAuthorisationJourneyRequest: JourneyRequest[?] = new JourneyRequest(journey = agentCancelAuthorisationJourney, request)
  val controller: SelectClientTypeController = app.injector.instanceOf[SelectClientTypeController]
  val journeyService: JourneyService = app.injector.instanceOf[JourneyService]

  override def beforeEach(): Unit = {
    await(journeyService.deleteAllAnswersInSession(request))
    super.beforeEach()
  }

  "GET /authorisation-request/client-type" should {
    "display the select client type page" in {
      await(journeyService.saveJourney(authorisationRequestJourney))
      val result: Future[Result] = controller.show(JourneyType.AuthorisationRequest)(request)
      status(result) shouldBe OK
    }
  }

  "POST /authorisation-request/client-type" should {
    "show an error when no selection is made" in {
      await(journeyService.saveJourney(authorisationRequestJourney))
      val result: Future[Result] = controller.onSubmit(journeyType = JourneyType.AuthorisationRequest)(request)
      status(result) shouldBe BAD_REQUEST
    }
  }

  "GET /agent-cancel-authorisation/client-type" should {
    "display the select client type page" in {
      await(journeyService.saveJourney(agentCancelAuthorisationJourney))
      val result: Future[Result] = controller.show(journeyType = JourneyType.AgentCancelAuthorisation)(request)
      status(result) shouldBe OK
    }
  }

  "POST /agent-cancel-authorisation/client-type" should {
    "show an error when no selection is made" in {
      await(journeyService.saveJourney(agentCancelAuthorisationJourney))
      val result: Future[Result] = controller.onSubmit(journeyType = JourneyType.AgentCancelAuthorisation)(request)
      status(result) shouldBe BAD_REQUEST
    }
  }

}
