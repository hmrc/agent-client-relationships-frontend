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
      val date = LocalDate.parse("2022-06-11")
      displayDateForLang(Some(date)) shouldBe "11 Mehefin 2022"
    }
  }

  "display the date correctly for an ExitPage" in {
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    val date: Instant = LocalDateTime.of(2024, 1, 10, 0, 0).toInstant(ZoneOffset.UTC)
    displayInstant(date) shouldBe "10 January 2024"
  }
}
