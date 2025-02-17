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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.client

import play.api.data.Form
import play.api.data.Forms.{boolean, optional, single}
import play.api.i18n.Messages
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.DeclineRequestFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.FormFieldHelper

object DeclineRequestForm extends FormFieldHelper {
  def form(agentName: String)(implicit messages: Messages): Form[Boolean] = Form(
    single(
      DeclineRequestFieldName -> optional(boolean)
        .verifying(mandatoryBoolean(DeclineRequestFieldName, agentName))
        .transform(_.getOrElse(false), Some(_))
    )
  )
}