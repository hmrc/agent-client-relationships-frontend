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

package uk.gov.hmrc.agentclientrelationshipsfrontend.controllers.aif

import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.ComponentSpecHelper

class InvitationsRedirectControllerISpec extends ComponentSpecHelper:

  override val baseUrl = "/invitations"


  "redirect" should:
    "redirect agents to track page" in {

      val result = get("/agents/client-type")
      result.status shouldBe 303
      result.header("Location").value shouldBe "/agent-client-relationships/manage-authorisation-requests"
    }

    "redirect clients to myta page" in {

      val result = get("/personal-taxes/")
      result.status shouldBe 303
      result.header("Location").value shouldBe "/agent-client-relationships/manage-your-tax-agents"
    }

