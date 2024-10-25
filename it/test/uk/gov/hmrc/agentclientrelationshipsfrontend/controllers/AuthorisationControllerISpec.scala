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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers

import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.UserTimedOut
import uk.gov.hmrc.agentclientrelationshipsfrontend.views.html.auth.{CannotConfirmIdentity, IvLockedOut, IvTechDifficulties, NotAuthorisedAsClient}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class AuthorisationControllerISpec extends ComponentSpecHelper {

  val controller: AuthorisationController = app.injector.instanceOf[AuthorisationController]

  implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(request)

  val testUrl = "/url"
  val testJourneyId = "123"

  def ivUrl(id: String) = s"/mdtp/journey/journeyId/$id"

  "GET /cannot-view-request" should {
    "return NOT_IMPLEMENTED" in {
      val result = controller.cannotViewRequest(request)
      lazy val view = app.injector.instanceOf[NotAuthorisedAsClient]

      status(result) shouldBe FORBIDDEN
      contentAsString(result) shouldBe view().body
    }
  }

  "GET /cannot-confirm-identity" should {
    "return FORBIDDEN with the CannotConfirmIdentity view when there's no journeyId" in {
      val result = controller.cannotConfirmIdentity(None, Some(RedirectUrl(testUrl)))(request)
      lazy val view = app.injector.instanceOf[CannotConfirmIdentity]

      status(result) shouldBe FORBIDDEN
      contentAsString(result) shouldBe view(Some(testUrl)).body
    }
    "return FORBIDDEN with the IvTechDifficulties view when the journey status is TechnicalIssue" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "TechnicalIssue").toString)

      val result = controller.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl)))(request)
      lazy val view = app.injector.instanceOf[IvTechDifficulties]

      status(result) shouldBe FORBIDDEN
      contentAsString(result) shouldBe view().body
    }
    "redirect to /iv-timed-out when the journey status is TimedOut" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "Timeout").toString)

      val result = controller.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl)))(request)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.AuthorisationController.ivTimedOut(Some(RedirectUrl(testUrl))).url)
    }
    "redirect to /iv-locked-out when the journey status is LockedOut" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "LockedOut").toString)

      val result = controller.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl)))(request)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.AuthorisationController.ivLockedOut.url)
    }
    "return FORBIDDEN with the CannotConfirmIdentity view for any other journey status" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "InsufficientEvidence").toString)

      val result = controller.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl)))(request)
      lazy val view = app.injector.instanceOf[CannotConfirmIdentity]

      status(result) shouldBe FORBIDDEN
      contentAsString(result) shouldBe view(Some(testUrl)).body
    }
  }

  "GET /iv-timed-out" should {
    "return FORBIDDEN with the TimedOut view" in {
      val result = controller.ivTimedOut(Some(RedirectUrl(testUrl)))(request)
      lazy val view = app.injector.instanceOf[UserTimedOut]

      status(result) shouldBe FORBIDDEN
      contentAsString(result) shouldBe view(Some(testUrl), isAgent = false).body
    }
  }

  "GET /iv-locked-out" should {
    "return FORBIDDEN with the IvLockedOut view" in {
      val result = controller.ivLockedOut()(request)
      lazy val view = app.injector.instanceOf[IvLockedOut]

      status(result) shouldBe FORBIDDEN
      contentAsString(result) shouldBe view().body
    }
  }
}
