/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.http.Status.SEE_OTHER
import uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client.routes as clientRoutes
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import sttp.model.Uri.UriContext
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class SignOutControllerISpec extends ComponentSpecHelper with AuthStubs {

  def makeSignOutUrl(continueUrl: String): String =
    uri"http://localhost:9099/bas-gateway/sign-out-without-state?${Map("continue" -> continueUrl)}".toString

  "GET /sign-out" should {
    "sign out and redirect to continueUrl when it is provided" in {
      authoriseAsAgent()
      val expectedContinueUrl = "/url/continue"
      val result = get(routes.SignOutController.signOut(Some(RedirectUrl(expectedContinueUrl)), true).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe makeSignOutUrl(expectedContinueUrl)
    }
    "sign out and redirect to ASA if user is an agent" in {
      authoriseAsAgent()
      val expectedContinueUrl = "http://localhost:9401/agent-services-account/home"
      val result = get(routes.SignOutController.signOut(isAgent = true).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe makeSignOutUrl(expectedContinueUrl)
    }
    "sign out redirect to MYTA if user is a client" in {
      authoriseAsClient()
      val expectedContinueUrl = clientRoutes.ManageYourTaxAgentsController.show.url
      val result = get(routes.SignOutController.signOut(isAgent = false).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe makeSignOutUrl("http://localhost:9435" + expectedContinueUrl)
    }
  }

}
