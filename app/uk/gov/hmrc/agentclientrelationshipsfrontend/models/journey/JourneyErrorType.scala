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

enum JourneyErrorType(val name: String):
  case NotFound extends JourneyErrorType("client-not-found")
  case NotRegistered extends JourneyErrorType("not-registered")
  case ClientAlreadyInvited extends JourneyErrorType("already-authorisation-pending")
  case AuthorisationAlreadyExists extends JourneyErrorType("authorisation-already-exists")
  case NoAuthorisationExists extends JourneyErrorType("not-authorised")
  case ClientStatusInsolvent extends JourneyErrorType("client-insolvent")
  case ClientStatusInvalid extends JourneyErrorType("client-status-invalid")

  override def toString: String = name
