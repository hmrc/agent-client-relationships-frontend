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

import play.api.data.Forms.{optional, text}
import play.api.data.Mapping
import play.api.data.validation.*

object TextFormFieldHelper extends FormFieldHelper {

  val emptyMapping: (String, Mapping[String]) = "" -> optional(text).transform(_.getOrElse(""), (Some(_)): String => Option[String])
  
  def textFieldMapping(fieldName: String, formMessageKey: String, regex: String = ".*"): Mapping[String] = {
    optional(text)
      .verifying(validateText(fieldName, formMessageKey, regex))
      .transform(_.getOrElse(""), (Some(_)): String => Option[String])
  }

  private def validateText(fieldName: String, formMessageKey: String, regex: String = ".*"): Constraint[Option[String]] = {
    Constraint[Option[String]] { (userInput: Option[String]) =>
      userInput match {
        case Some(value) if value.nonEmpty && value.matches(regex) => Valid
        case Some(value) if value.nonEmpty && !value.matches(regex) => invalidInput(formMessageKey, fieldName)
        case _ => invalidMandatoryField(formMessageKey, fieldName)
      }
    }
  }

}
