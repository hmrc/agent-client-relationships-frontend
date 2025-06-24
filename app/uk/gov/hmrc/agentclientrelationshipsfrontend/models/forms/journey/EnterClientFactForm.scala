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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey

import play.api.data.Form
import play.api.data.Forms.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.common.KnownFactsConfiguration
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.DateFormFieldHelper.dateFieldMapping
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.FormFieldHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.TextFormFieldHelper.textFieldMapping

object EnterClientFactForm extends FormFieldHelper {
  def form(fieldConfig: KnownFactsConfiguration, serviceName: String): Form[String] = {
    Form(
      single(
        (fieldConfig.inputType, fieldConfig.validOptions) match {
          case ("date", _) =>
            fieldConfig.name -> dateFieldMapping(s"clientFact.$serviceName.${fieldConfig.name}")
          case ("select", Some(options)) =>
            fieldConfig.name -> optional(text)
              .verifying(mandatoryFieldErrorMessage(s"clientFact.$serviceName.${fieldConfig.name}"), _.fold(false)(options.map(_._1).toSet.contains))
              .transform(_.getOrElse(""), (Some(_)): String => Option[String])
          case ("text", _) =>
            fieldConfig.name -> textFieldMapping(fieldConfig.name, s"clientFact.$serviceName.${fieldConfig.name}", fieldConfig.regex)
          case _ =>
            throw RuntimeException(s"Attempted to create an unsupported form - input type: ${fieldConfig.inputType}, options: ${fieldConfig.validOptions}")
        }
      )
    )
  }
}
