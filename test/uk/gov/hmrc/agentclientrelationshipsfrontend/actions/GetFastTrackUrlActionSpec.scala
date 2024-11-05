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
import org.scalatest.RecoverMethods.recoverToExceptionIf
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json, OFormat}
import play.api.mvc.*
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.SsoConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.RequestSupport
import uk.gov.hmrc.http.BadRequestException

import scala.concurrent.{ExecutionContext, Future}

class GetFastTrackUrlActionSpec extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach with GuiceOneAppPerSuite:

  val fakeAuthAction: ActionRefiner[Request, AgentRequest] = new ActionRefiner[Request, AgentRequest] {
    override def refine[A](request: Request[A]): Future[Either[Result, AgentRequest[A]]] =
      Future.successful(Right[Result, AgentRequest[A]](AgentRequest("testArn", request)))
    override protected def executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  }

  case class TestFastTrackUrls(redirectUrl: Option[String], errorUrl: Option[String], refererUrl: Option[String])
  object TestFastTrackUrls{
    implicit lazy val format: OFormat[TestFastTrackUrls] = Json.format[TestFastTrackUrls]
  }

  class FakeController(getFastTrackUrlAction: GetFastTrackUrlAction,
                       actionBuilder: DefaultActionBuilder) {
    def route: Action[AnyContent] =
      (actionBuilder andThen fakeAuthAction andThen getFastTrackUrlAction.getFastTrackUrlAction) {
        agentFastTrackRequest =>
          Results.Ok(Json.toJson(TestFastTrackUrls(
            redirectUrl = agentFastTrackRequest.redirectUrl,
            refererUrl = agentFastTrackRequest.refererUrl,
            errorUrl =agentFastTrackRequest.errorUrl)).toString)
      }
  }

  val mockSsoConnector: SsoConnector = mock[SsoConnector]
  val requestSupport: RequestSupport = app.injector.instanceOf[RequestSupport]
  val defaultActionBuilder: DefaultActionBuilder = app.injector.instanceOf[DefaultActionBuilder]

  given ExecutionContext = app.injector.instanceOf[ExecutionContext]

  override def afterEach(): Unit = {
    reset(mockSsoConnector)
    super.afterEach()
  }

  val testController = new FakeController(
    new GetFastTrackUrlAction(mockSsoConnector, requestSupport),
    defaultActionBuilder
  )


  "fastTrackAction" should {
    "successfully retrieve fastTrack URLs with all set to None" in {
      val allowedDomains = Set("localhost")
      val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
      val testFastTrackUrlsNone = TestFastTrackUrls(None, None, None)

      when(mockSsoConnector.getAllowlistedDomains()(any(), any()))
        .thenReturn(Future.successful(allowedDomains))

      val result = testController.route(fakeRequest)

      status(result) shouldBe OK
      contentAsJson(result).as[TestFastTrackUrls] shouldBe testFastTrackUrlsNone

    }

    "successfully retrieve fastTrack URLs with errorUrl set" in {
      val allowedDomains = Set("localhost")
      val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(call = Call("POST", s"/agent-client-relationships/agents/fast-track?error=http%3A%2F%2Flocalhost%3A9448%2Ftest-only%2Ffast-track"))
      val testFastTrackUrlsNone = TestFastTrackUrls(None, Some("http://localhost:9448/test-only/fast-track"), None)

      when(mockSsoConnector.getAllowlistedDomains()(any(), any()))
        .thenReturn(Future.successful(allowedDomains))

      val result = testController.route(fakeRequest)

      status(result) shouldBe OK
      contentAsJson(result).as[TestFastTrackUrls] shouldBe testFastTrackUrlsNone
    }

    "successfully retrieve fastTrack URLs with refferer set" in {
      val allowedDomains = Set("localhost")
      val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
        call = Call("POST", s"/agent-client-relationships/agents/fast-track?error=http%3A%2F%2Flocalhost%3A9448%2Ftest-only%2Ffast-track"))
        .withHeaders(FakeHeaders(Seq(HeaderNames.REFERER -> "http://localhost:9448/test-only/fast-track")))

      val testFastTrackUrlsNone = TestFastTrackUrls(None, Some("http://localhost:9448/test-only/fast-track"), Some("http://localhost:9448/test-only/fast-track"))

      when(mockSsoConnector.getAllowlistedDomains()(any(), any()))
        .thenReturn(Future.successful(allowedDomains))

      val result = testController.route(fakeRequest)

      status(result) shouldBe OK
      contentAsJson(result).as[TestFastTrackUrls] shouldBe testFastTrackUrlsNone
    }

    "return BadRequest when domain is not allowed" in {

      val allowedDomains = Set("NotAllowed")
      val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(call = Call("POST", s"/agent-client-relationships/agents/fast-track?error=http%3A%2F%2Flocalhost%3A9448%2Ftest-only%2Ffast-track"))
      val testFastTrackUrlsNone = TestFastTrackUrls(None, Some("http://localhost:9448/test-only/fast-track"), None)

      when(mockSsoConnector.getAllowlistedDomains()(any(), any()))
        .thenReturn(Future.successful(allowedDomains))

      val result = testController.route(fakeRequest)

      recoverToExceptionIf[BadRequestException] {
        result
      }
    }

  }
