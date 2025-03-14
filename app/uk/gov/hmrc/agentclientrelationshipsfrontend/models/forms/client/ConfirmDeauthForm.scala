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
import uk.gov.hmrc.agentclientrelationshipsfrontend.config.Constants.ConfirmDeauthFieldName
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.FormFieldHelper

object ConfirmDeauthForm extends FormFieldHelper {
  def form: Form[Boolean] = Form(
    single(
      ConfirmDeauthFieldName -> optional(boolean)
        .verifying(mandatoryBoolean(ConfirmDeauthFieldName))
        .transform(_.getOrElse(false), Some(_))
    )
  )
}
