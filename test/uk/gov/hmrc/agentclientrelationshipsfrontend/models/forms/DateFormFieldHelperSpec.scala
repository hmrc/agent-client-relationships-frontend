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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.Form
import play.api.data.Forms.single
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.DateFormFieldHelper

class DateFormFieldHelperSpec extends AnyWordSpecLike with Matchers {

  "The date form mapping" should {

    val mapping = DateFormFieldHelper.dateFieldMapping("exampleMsg")
    val form = Form(single("date" -> mapping))
    val exampleData = Map("date.day" -> "1", "date.month" -> "1", "date.year" -> "2020")

    "return a valid result when binding with appropriate day/month/year fields" in {
      form.bind(exampleData).hasErrors shouldBe false
    }

    "return a validation error when binding without day/month/year fields" in {
      form.bind(Map("date.something" -> "1")).hasErrors shouldBe true
    }

    "parse and transform a valid date string when it is filled to the form" in {
      form.fill("2020-01-01").data shouldBe exampleData
    }

    "fail to parse an invalid date string when it is filled to the form" in {
      intercept[IllegalArgumentException](form.fill(""))
    }
  }
}
