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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.client

import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status.OK
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.ClientExitType
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.{AuthStubs, ComponentSpecHelper}
import uk.gov.hmrc.http.HeaderCarrier

class ClientExitControllerISpec extends ComponentSpecHelper with AuthStubs with ScalaFutures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  
  ClientExitType.values.foreach(exitType =>
    s"GET /authorisation-response/exit/$exitType?" should {
      "display the exit page" in {
        authoriseAsClient()
        val result = get(routes.ClientExitController.show(exitType,normalizedAgentName = Some("")).url)
        result.status shouldBe OK
      }
    })
}