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

import play.api.http.Status.{OK, SEE_OTHER}
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import sttp.model.Uri.UriContext
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class TimedOutControllerISpec extends ComponentSpecHelper with AuthStubs {

  "GET /time-out" should {
    "redirect to bas-gateway-frontend/sign-out-without-state" in {
      val timedOutPageUrl = uri"""${"http://localhost:9435" + routes.TimedOutController.timedOut(RedirectUrl("/agent-client-relationships"), "Agents", isAgent = true).url}"""
      val signOutUrl = uri"http://localhost:9099/bas-gateway/sign-out-without-state?${Map("continue" -> timedOutPageUrl)}"
      val result = get(routes.TimedOutController.doTimeOut(RedirectUrl("/agent-client-relationships"), "Agents", isAgent = true).url)
      result.status shouldBe SEE_OTHER
      result.header("LOCATION").value shouldBe signOutUrl.toString
    }
  }

  "GET /timed-out" should {
    "return OK" in {
      val result = get(routes.TimedOutController.timedOut(RedirectUrl("/agent-client-relationships"), "Agents", isAgent = true).url)
      result.status shouldBe OK
    }
  }

}
