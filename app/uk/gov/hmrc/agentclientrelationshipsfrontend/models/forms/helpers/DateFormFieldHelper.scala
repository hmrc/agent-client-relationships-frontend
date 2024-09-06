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

import play.api.data.Forms.{text, tuple}
import play.api.data.Mapping
import play.api.data.format.Formats.*
import play.api.data.validation.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.*

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, ResolverStyle}

object DateFormFieldHelper extends FormFieldHelper {

  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT)
  
  private def parseDate(date: String): Boolean =
    try {
      LocalDate.parse(date, dateFormatter)
      true
    } catch {
      case _: Throwable => false
    }

  def dateFieldMapping(formMessageKey: String): Mapping[String] = {
    tuple(
      Year -> text,
      Month -> text,
      Day -> text
    ).verifying(validateDate(formMessageKey))
      .transform[String](
        { case (y, m, d) =>
          if (y.isEmpty || m.isEmpty || d.isEmpty) ""
          else LocalDate.of(y.toInt, m.toInt, d.toInt).format(dateFormatter)
        },
        date =>
          try {
            val l = LocalDate.parse(date)
            (l.getYear.toString, l.getMonthValue.toString, l.getDayOfMonth.toString)
          } catch {
            case e: Exception => throw new IllegalArgumentException(s"unexpected date input pattern $e")
          }
      )
  }

  private def validateDate(formMessageKey: String): Constraint[(String, String, String)] = {
    Constraint[(String, String, String)] { (s: (String, String, String)) =>
      (s._1.nonEmpty, s._2.nonEmpty, s._3.nonEmpty) match {
        //   (year, month, day)
        case (false, false, false) => invalidMandatoryField(formMessageKey, s"$Day-$Month-$Year")
        case (true, true, false) => invalidMandatoryField(s"$formMessageKey.$Day", Day)
        case (true, false, true) => invalidMandatoryField(s"$formMessageKey.$Month", Month)
        case (false, true, true) => invalidMandatoryField(s"$formMessageKey.$Year", Year)
        case (true, false, false) => invalidMandatoryField(s"$formMessageKey.$Day-$Month", s"$Day-$Month")
        case (false, true, false) => invalidMandatoryField(s"$formMessageKey.$Day-$Year", s"$Day-$Year")
        case (false, false, true) => invalidMandatoryField(s"$formMessageKey.$Month-$Year", s"$Month-$Year")
        case (true, true, true) =>
          if (parseDate(s"${s._1}-${s._2}-${s._3}")) Valid
          else invalidInput(formMessageKey, s"$Day-$Month-$Year")
      }
    }
  }

}
