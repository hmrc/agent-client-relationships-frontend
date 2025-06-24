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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.KnownFactsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey.EnterClientFactForm


class EnterClientFactFormSpec extends AnyWordSpecLike with Matchers :

  "The EnterClientFactForm" should :

    "have the correct form mapping type" when :

      "the input type is 'date'" in :
        val fieldConfig: KnownFactsConfiguration = KnownFactsConfiguration("fieldName", "", "date", 1, None)
        val form = EnterClientFactForm.form(fieldConfig, "service")
        val result = form.bind(Map("fieldName.day" -> "11", "fieldName.month" -> "11", "fieldName.year" -> "2000"))
        result.hasErrors shouldBe false

      "the input type is 'select'" in :
        val fieldConfig: KnownFactsConfiguration = KnownFactsConfiguration("fieldName", "", "select", 1, Some(Seq("AL" -> "Albania")))
        val form = EnterClientFactForm.form(fieldConfig, "service")
        val result = form.bind(Map("fieldName" -> "AL"))
        result.hasErrors shouldBe false

      "the input type is 'text'" in :
        val fieldConfig: KnownFactsConfiguration = KnownFactsConfiguration("fieldName", "[A-Z]{3}", "text", 3, None)
        val form = EnterClientFactForm.form(fieldConfig, "service")
        val result = form.bind(Map("fieldName" -> "ABC"))
        result.hasErrors shouldBe false

    "throw an exception" when :

      "the input type is unrecognised" in :
        val fieldConfig: KnownFactsConfiguration = KnownFactsConfiguration("fieldName", "", "music", 1, None)
        intercept[RuntimeException](EnterClientFactForm.form(fieldConfig, "service"))