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

package uk.gov.hmrc.agentclientrelationshipsfrontend.config

import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ServiceConstants

object Constants extends ServiceConstants {
  
  // agent enrolment
  val AsAgent = "HMRC-AS-AGENT"
  
  // client services
  val MtdIncomeTax = "HMRC-MTD-IT"
  val PersonalIncome = "PERSONAL-INCOME-RECORD"
  val PlasticPackaging = "HMRC-PPT-ORG"
  val CgtPd = "HMRC-CGT-PD"

  // dates
  val Year = "year"
  val Month = "month"
  val Day = "day"

  // createInvitationFields
  val ClientTypeFieldName = "clientType"
  val ClientServiceFieldName = "clientService"
  val ClientNameFieldName = "clientName"
  val ClientConfirmationFieldName = "confirmClient"

  val AgentRoleFieldName = "agentRole"
  val ConfirmCancellationFieldName = "confirmCancellation"
  val ConfirmConsentFieldName = "confirmConsent"
  val DeclineRequestFieldName = "confirmDecline"
  val ConfirmDeauthFieldName = "clientConfirmDeauth"

}
