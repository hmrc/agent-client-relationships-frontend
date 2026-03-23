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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey

case class CheckYourAnswersAuthorisationRequestData(
  service: String
)

case class CheckYourAnswersSubmitAuthorisationRequestData(
  journey: AgentJourney
)

case class CheckYourAnswersSubmitAgentCancelAuthorisationData(
  journey: AgentJourney,
  clientName: String,
  activeRelationship: String
)

enum CheckYourAnswersShowEntry:
  case RedirectToAgentServicesAccount
  case RedirectToSelectClientType
  case RedirectToEnterClientId
  case Exit(exitType: JourneyExitType)
  case ShowAuthorisationRequest(data: CheckYourAnswersAuthorisationRequestData)
  case ShowAgentCancelAuthorisation

enum CheckYourAnswersSubmitEntry:
  case RedirectToConfirmation
  case RedirectToSelectClientType
  case RedirectToEnterClientId
  case Exit(exitType: JourneyExitType)
  case SubmitAuthorisationRequest(data: CheckYourAnswersSubmitAuthorisationRequestData)
  case SubmitAgentCancelAuthorisation(data: CheckYourAnswersSubmitAgentCancelAuthorisationData)
