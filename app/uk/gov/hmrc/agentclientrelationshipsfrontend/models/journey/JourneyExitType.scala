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

enum JourneyExitType(val name: String):
  case NotFound extends JourneyExitType("client-not-found")
  case NotRegistered extends JourneyExitType("not-registered")
  case ClientAlreadyInvited extends JourneyExitType("pending-authorisation-exists")
  case AuthorisationAlreadyExists extends JourneyExitType("authorisation-already-exists")
  case NoAuthorisationExists extends JourneyExitType("not-authorised")
  case ClientStatusInsolvent extends JourneyExitType("client-insolvent")
  case ClientStatusInvalid extends JourneyExitType("client-status-invalid")
  case NoChangeOfAgentRole extends JourneyExitType("no-change-of-agent-role")

  override def toString: String = name
