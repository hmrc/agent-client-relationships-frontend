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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.FormError

import java.time.LocalDate

class LocalDateFormatterSpec extends AnyWordSpec with Matchers {

  val msgPrefix = "page.service"

  val formatter = new LocalDateFormatter(msgPrefix)

  ".validateDayMonthYear" should {

    "return no form errors when all date values are valid" in {
      formatter.validateDayMonthYear("date", Some("11"), Some("11"), Some("2000")) shouldBe Nil
    }

    "return form errors" when {

      "the day is invalid" in {
        formatter.validateDayMonthYear("date", Some("f"), Some("11"), Some("2000")) shouldBe
          Seq(FormError("date.day", formatter.invalidKey, Seq()))
      }

      "the month is invalid" in {
        formatter.validateDayMonthYear("date", Some("11"), Some("f"), Some("2000")) shouldBe
          Seq(FormError("date.month", formatter.invalidKey, Seq()))
      }

      "the year is invalid" in {
        formatter.validateDayMonthYear("date", Some("11"), Some("11"), Some("f")) shouldBe
          Seq(FormError("date.year", formatter.invalidKey, Seq()))
      }

      "the month and year are invalid" in {
        formatter.validateDayMonthYear("date", Some("11"), Some("20"), Some("0")) shouldBe Seq(
          FormError("date.month", formatter.invalidKey, Seq()),
          FormError("date.year", formatter.invalidKey, Seq()))
      }

      "the day and year are invalid" in {
        formatter.validateDayMonthYear("date", Some("50"), Some("11"), Some("0")) shouldBe Seq(
          FormError("date.day", formatter.invalidKey, Seq()),
          FormError("date.year", formatter.invalidKey, Seq()))
      }

      "the day and month are invalid" in {
        formatter.validateDayMonthYear("date", Some("5.6"), Some("9.9"), Some("2000")) shouldBe Seq(
          FormError("date.day", formatter.invalidKey, Seq()),
          FormError("date.month", formatter.invalidKey, Seq()))
      }

      "all three fields are invalid" in {
        formatter.validateDayMonthYear("date", Some("&%£"), Some("~@;"), Some("-_-")) shouldBe
          Seq(FormError("date", formatter.invalidKey, Seq()))
      }

      "the day is empty" in {
        formatter.validateDayMonthYear("date", None, Some("11"), Some("2000")) shouldBe
          Seq(FormError("date.day", formatter.dayRequiredKey, Seq()))
      }

      "the month is empty" in {
        formatter.validateDayMonthYear("date", Some("11"), None, Some("2000")) shouldBe
          Seq(FormError("date.month", formatter.monthRequiredKey, Seq()))
      }

      "the year is empty" in {
        formatter.validateDayMonthYear("date", Some("11"), Some("11"), None) shouldBe
          Seq(FormError("date.year", formatter.yearRequiredKey, Seq()))
      }

      "the month and year are empty" in {
        formatter.validateDayMonthYear("date", Some("11"), None, None) shouldBe Seq(
          FormError("date.month", formatter.monthYearRequiredKey, Seq()),
          FormError("date.year", formatter.monthYearRequiredKey, Seq()))
      }

      "the day and year are empty" in {
        formatter.validateDayMonthYear("date", None, Some("11"), None) shouldBe Seq(
          FormError("date.day", formatter.dayYearRequiredKey, Seq()),
          FormError("date.year", formatter.dayYearRequiredKey, Seq()))
      }

      "the day and month are empty" in {
        formatter.validateDayMonthYear("date", None, None, Some("2000")) shouldBe Seq(
          FormError("date.day", formatter.dayMonthRequiredKey, Seq()),
          FormError("date.month", formatter.dayMonthRequiredKey, Seq()))
      }

      "all three fields are empty" in {
        formatter.validateDayMonthYear("date", None, None, None) shouldBe
          Seq(FormError("date", formatter.allRequiredKey, Seq()))
      }

      "there are a mix of invalid and empty fields (empty takes precedence)" in {
        formatter.validateDayMonthYear("date", Some("f"), None, Some("f")) shouldBe
          Seq(FormError("date.month", formatter.monthRequiredKey, Seq()))
      }
    }
  }

  ".toDate" should {

    "return a LocalDate when the provided values make a valid date" in {
      formatter.toDate("date", 11, 11, 2000) shouldBe Right(LocalDate.parse("2000-11-11"))
    }

    "return a form error when the provided values do not make a valid date" in {
      formatter.toDate("date", 31, 11, 2000) shouldBe Left(Seq(FormError("date", formatter.invalidKey)))
    }
  }

  ".bind" should {

    "return a LocalDate when binding was successful" in {
      formatter.bind("date", Map("date.day" -> "11", "date.month" -> "11", "date.year" -> "2000")) shouldBe
        Right(LocalDate.parse("2000-11-11"))
    }

    "return the form errors when binding was unsuccessful" in {
      formatter.bind("date", Map("date.day" -> "fff", "date.month" -> "79", "date.year" -> "3.142")) shouldBe
        Left(Seq(FormError("date", formatter.invalidKey)))
    }
  }

  ".unbind" should {

    "return form data from a LocalDate" in {
      formatter.unbind("date", LocalDate.parse("2000-11-11")) shouldBe Map(
        "date.day" -> "11",
        "date.month" -> "11",
        "date.year" -> "2000"
      )
    }
  }
}
