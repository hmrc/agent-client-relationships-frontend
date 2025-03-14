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

import play.api.http.Status.OK
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class TimedOutControllerISpec extends ComponentSpecHelper with AuthStubs {

  "GET /timed-out" should {
    "return OK" in {
      val result = get(routes.TimedOutController.timedOut(RedirectUrl("/agent-client-relationships"), "Agents").url)
      result.status shouldBe OK
    }
  }

}
