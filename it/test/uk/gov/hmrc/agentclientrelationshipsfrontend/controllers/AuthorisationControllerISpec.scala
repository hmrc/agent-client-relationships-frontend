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
import play.api.libs.ws.{BodyReadable, DefaultBodyReadables}
import play.api.test.Helpers.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.AppConfig
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.WiremockHelper.stubGet
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class AuthorisationControllerISpec extends ComponentSpecHelper {

  given BodyReadable[String] = DefaultBodyReadables.readableAsString

  implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(request)

  val testUrl = "/url"
  val testJourneyId = "123"

  def ivUrl(id: String) = s"/mdtp/journey/journeyId/$id"

  "GET /cannot-view-request" should {
    "return NOT_IMPLEMENTED" in {
      val result = get(routes.AuthorisationController.cannotViewRequest.url)

      result.status shouldBe FORBIDDEN
    }
  }

  "GET /cannot-confirm-identity" should {
    "return FORBIDDEN with the CannotConfirmIdentity view when there's no journeyId" in {
      val result = get(routes.AuthorisationController.cannotConfirmIdentity(None, Some(RedirectUrl(testUrl))).url)

      result.status shouldBe FORBIDDEN
    }
    "return FORBIDDEN with the IvTechDifficulties view when the journey status is TechnicalIssue" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "TechnicalIssue").toString)

      val result = get(routes.AuthorisationController.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl))).url)

      result.status shouldBe FORBIDDEN
    }
    "redirect to /iv-timed-out when the journey status is TimedOut" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "Timeout").toString)

      val result = get(routes.AuthorisationController.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl))).url)

      result.status shouldBe SEE_OTHER
      result.header("Location") shouldBe Some(routes.AuthorisationController.ivTimedOut(Some(RedirectUrl(testUrl))).url)
    }
    "redirect to /iv-locked-out when the journey status is LockedOut" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "LockedOut").toString)

      val result = get(routes.AuthorisationController.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl))).url)

      result.status shouldBe SEE_OTHER
      result.header("Location") shouldBe Some(routes.AuthorisationController.ivLockedOut.url)
    }
    "return FORBIDDEN with the CannotConfirmIdentity view for any other journey status" in {
      stubGet(ivUrl(testJourneyId), OK, Json.obj("result" -> "InsufficientEvidence").toString)

      val result = get(routes.AuthorisationController.cannotConfirmIdentity(Some(testJourneyId), Some(RedirectUrl(testUrl))).url)

      result.status shouldBe FORBIDDEN
    }
  }

  "GET /iv-timed-out" should {
    "return FORBIDDEN with the TimedOut view" in {
      val result = get(routes.AuthorisationController.ivTimedOut(Some(RedirectUrl(testUrl))).url)

      result.status shouldBe FORBIDDEN
    }
  }

  "GET /iv-locked-out" should {
    "return FORBIDDEN with the IvLockedOut view" in {
      val result = get(routes.AuthorisationController.ivLockedOut.url)

      result.status shouldBe FORBIDDEN
    }
  }
}
