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

package uk.gov.hmrc.agentclientrelationshipsfrontend.utils

import play.api.i18n.Lang
import play.api.mvc.Request

import java.time.{Instant, LocalDate, ZoneId}
import java.time.format.DateTimeFormatter
import scala.util.Try

object DisplayDate {

  def displayDateForLang(date: Option[java.time.LocalDate])(implicit request: Request[?]): String = {
    val lang = request.cookies
      .get("PLAY_LANG")
      .map(_.value)
      .getOrElse("en")

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Lang(lang).toLocale)

    date.map(_.format(dateFormatter)).getOrElse("")
    }

  def displayDateForLangFromString(strDate: String)(implicit request: Request[?]): String = {
    val localDate = Try(LocalDate.parse(strDate))
    localDate.map(d => displayDateForLang(Some(d))).getOrElse(strDate)
  }

  def displayInstant(instant: Instant)(implicit request: Request[?]): String = {
    val localDate = instant.atZone(ZoneId.of("Europe/London")).toLocalDate
    displayDateForLang(Some(localDate))
  }
}
