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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client

import play.api.data.*
import play.api.data.Forms.*
import play.api.i18n.Messages
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ConfirmConsentFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.FormFieldHelper

object ConfirmConsentForm extends FormFieldHelper {
  def form(agentName: String, agentRole: String, service: String)(implicit messages: Messages): Form[Boolean] = Form(
    single(
      ConfirmConsentFieldName -> optional(boolean)
        .verifying(mandatoryBoolean(ConfirmConsentFieldName, agentName, messages(s"$ConfirmConsentFieldName.form.$agentRole"), messages(service)))
        .transform(_.getOrElse(false), Some(_))
    )
  )
}
