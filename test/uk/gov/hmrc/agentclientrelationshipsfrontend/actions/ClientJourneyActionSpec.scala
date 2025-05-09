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

package uk.gov.hmrc.agentclientrelationshipsfrontend.actions

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.libs.json.Json
import play.api.mvc.*
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client.routes
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.ClientJourney
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.{AgentJourneyService, ClientJourneyService}

import scala.concurrent.{ExecutionContext, Future}

class ClientJourneyActionSpec extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach with GuiceOneAppPerSuite:

  val fakeAuthAction: ActionRefiner[Request, Request] = new ActionRefiner[Request, Request] {
    override def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] =
      Future.successful(Right[Result, Request[A]](request))

    override protected def executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  }

  class FakeController(getJourneyAction: GetJourneyAction,
                       actionBuilder: DefaultActionBuilder) {
    def route(journeyRequired: Boolean): Action[AnyContent] =
      (actionBuilder andThen fakeAuthAction andThen getJourneyAction.clientJourneyAction(journeyRequired)) {
        journeyRequest =>
          Results.Ok(Json.toJson(journeyRequest.journey).toString)
      }
  }

  val mockAgentJourneyService: AgentJourneyService = mock[AgentJourneyService]
  val mockClientJourneyService: ClientJourneyService = mock[ClientJourneyService]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  val defaultActionBuilder: DefaultActionBuilder = app.injector.instanceOf[DefaultActionBuilder]

  given ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override def afterEach(): Unit = {
    reset(mockAgentJourneyService)
    reset(mockClientJourneyService)
    super.afterEach()
  }

  val testController = new FakeController(
    new GetJourneyAction(mockAgentJourneyService, mockClientJourneyService, appConfig),
    defaultActionBuilder
  )

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val testArn = "testArn"

  "clientJourneyAction" should {
    "successfully retrieve journey and continue" in {
      val testJourney = ClientJourney(
        "authorisation-response"
      )
      when(mockClientJourneyService.getJourney(any(), any()))
        .thenReturn(Future.successful(Some(testJourney)))

      val result = testController.route(false)(fakeRequest)

      status(result) shouldBe OK
      contentAsJson(result).as[ClientJourney] shouldBe testJourney
    }

    "create a new journey if one doesn't exist in Mongo" in {
      when(mockClientJourneyService.getJourney(any(), any()))
        .thenReturn(Future.successful(None))

      val result = testController.route(false)(fakeRequest)

      status(result) shouldBe OK
      contentAsJson(result).as[ClientJourney] shouldBe ClientJourney(journeyType = "authorisation-response")
    }

    "redirect to MYTA if journey doesn't exist in Mongo when required" in {
      when(mockClientJourneyService.getJourney(any(), any()))
        .thenReturn(Future.successful(None))

      val result = testController.route(true)(fakeRequest)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe routes.ManageYourTaxAgentsController.show.url
    }
  }


