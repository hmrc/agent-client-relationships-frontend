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
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class SignOutControllerISpec extends ComponentSpecHelper with AuthStubs {

  "GET /sign-out" should {
    "sign out and redirect to continueUrl when it is provided" in {
      authoriseAsAgent()
      val continueUrl = RedirectUrl("/url/continue")
      val result = get(routes.SignOutController.signOut(Some(continueUrl), true).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "/url/continue"
    }
    "sign out and redirect to ASA if user is an agent" in {
      authoriseAsAgent()
      val result = get(routes.SignOutController.signOut(isAgent = true).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe "http://localhost:9401/agent-services-account/home"
    }
    "sign out redirect to MYTA if user is a client" in {
      authoriseAsAgent()
      val result = get(routes.SignOutController.signOut(isAgent = false).url)
      result.status shouldBe SEE_OTHER
      result.header("Location").value shouldBe clientRoutes.ManageYourTaxAgentsController.show.url
    }
  }

}
