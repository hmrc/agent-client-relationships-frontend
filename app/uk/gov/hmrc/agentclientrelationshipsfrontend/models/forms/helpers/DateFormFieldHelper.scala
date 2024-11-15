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

import play.api.data.Forms.of
import play.api.data.Mapping

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

object DateFormFieldHelper {

  def dateFieldMapping(formMessageKey: String): Mapping[String] = {
    of(new LocalDateFormatter(formMessageKey)).transform(
      date => date.toString,
      dateString => Try(LocalDate.parse(dateString)) match {
        case Success(parsedDate) => parsedDate
        case Failure(_) => throw new IllegalArgumentException("An illegal date was filled to the form without validation")
      }
    )
  }
}