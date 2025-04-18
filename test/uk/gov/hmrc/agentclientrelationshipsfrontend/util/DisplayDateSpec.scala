/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.util

import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.mvc.{AnyContentAsEmpty, Cookie}
import play.api.test.FakeRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.utils.DisplayDate.*

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

class DisplayDateSpec extends AnyWordSpecLike with Matchers with OptionValues with ScalaFutures {

  "display date util" should {
    "display the date in English when no PLAY_LANG cookie available" in {
      implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
      val date = LocalDate.parse("2020-06-29")
      displayDateForLang(Some(date)) shouldBe "29 June 2020"
    }

    "display the date in English when PLAY_LANG cookie is en" in {
      implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withCookies(Cookie("PLAY_LANG", "en"))
      val date = LocalDate.parse("2020-02-28")
      displayDateForLang(Some(date)) shouldBe "28 February 2020"
    }

    "display the date correctly in Welsh when the PLAY_LANG cookie is cy" in {
      implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withCookies(Cookie("PLAY_LANG", "cy"))
      def parse(d: String): LocalDate = LocalDate.parse(d)

      val d1 = parse("2021-01-21")
      val d2 = parse("2021-02-12")
      val d3 = parse("2020-03-01")
      val d4 = parse("2020-04-30")
      val d5 = parse("2020-05-31")
      val d6 = parse("2022-06-01")
      val d7 = parse("2023-07-29")
      val d8 = parse("2022-08-31")
      val d9 = parse("2022-09-13")
      val d10 = parse("2022-10-07")
      val d11 = parse("2022-11-09")
      val d12 = parse("2022-12-03")

      displayDateForLang(Some(d1)) shouldBe "21 Ionawr 2021"
      displayDateForLang(Some(d2)) shouldBe "12 Chwefror 2021"
      displayDateForLang(Some(d3)) shouldBe "1 Mawrth 2020"
      displayDateForLang(Some(d4)) shouldBe "30 Ebrill 2020"
      displayDateForLang(Some(d5)) shouldBe "31 Mai 2020"
      displayDateForLang(Some(d6)) shouldBe "1 Mehefin 2022"
      displayDateForLang(Some(d7)) shouldBe "29 Gorffennaf 2023"
      displayDateForLang(Some(d8)) shouldBe "31 Awst 2022"
      displayDateForLang(Some(d9)) shouldBe "13 Medi 2022"
      displayDateForLang(Some(d10)) shouldBe "7 Hydref 2022"
      displayDateForLang(Some(d11)) shouldBe "9 Tachwedd 2022"
      displayDateForLang(Some(d12)) shouldBe "3 Rhagfyr 2022"

    }
  }

  "display the date correctly for an ExitPage" in {
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    val date: Instant = LocalDateTime.of(2024, 1, 10, 0, 0).toInstant(ZoneOffset.UTC)
    displayInstant(date) shouldBe "10 January 2024"
  }
}
