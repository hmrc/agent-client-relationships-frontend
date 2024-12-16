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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms

import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client.ConfirmConsentForm
import uk.gov.hmrc.agentclientrelationshipsfrontend.support.ViewSpecSupport

class ConfirmConsentFormSpec extends ViewSpecSupport:

  "The confirm consent form" should:

    val form = ConfirmConsentForm.form("Agent Name", "mainAgent", "HMRC-MTD-IT")

    "bind successfully" when:

      "the value is true" in:
        form.bind(Map("confirmConsent" -> "true")).hasErrors shouldBe false

      "the value is false" in:
        form.bind(Map("confirmConsent" -> "false")).hasErrors shouldBe false
  
    "fail to bind an empty value and return the expected message key and message args" when:

      "the agentRole param is 'agent'" in:
        val agentForm = ConfirmConsentForm.form("Agent Name", "agent", "HMRC-MTD-IT")
        val result = agentForm.bind(Map("confirmConsent" -> ""))
        result.hasErrors shouldBe true
        result.errors.head.message shouldBe "confirmConsent.error.required"
        result.errors.head.args shouldBe Seq("Agent Name", "agent", "Making Tax Digital for Income Tax")

      "the agentRole param is 'mainAgent'" in :
        val agentForm = ConfirmConsentForm.form("Agent Name", "mainAgent", "HMRC-MTD-IT")
        val result = agentForm.bind(Map("confirmConsent" -> ""))
        result.hasErrors shouldBe true
        result.errors.head.message shouldBe "confirmConsent.error.required"
        result.errors.head.args shouldBe Seq("Agent Name", "main agent", "Making Tax Digital for Income Tax")

      "the agentRole param is 'suppAgent'" in :
        val agentForm = ConfirmConsentForm.form("Agent Name", "suppAgent", "HMRC-MTD-IT")
        val result = agentForm.bind(Map("confirmConsent" -> ""))
        result.hasErrors shouldBe true
        result.errors.head.message shouldBe "confirmConsent.error.required"
        result.errors.head.args shouldBe Seq("Agent Name", "supporting agent", "Making Tax Digital for Income Tax")
