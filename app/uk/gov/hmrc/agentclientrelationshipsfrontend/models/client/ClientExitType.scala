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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.client

enum ClientExitType(val name: String):
  case AgentSuspended extends ClientExitType("agent-suspended")
  case NoOutstandingRequests extends ClientExitType("no-outstanding-requests")
  case CannotFindAuthorisationRequest extends ClientExitType("cannot-find-auth-request")
  case AuthorisationRequestExpired extends ClientExitType("auth-request-expired")
  case AuthorisationRequestCancelled extends ClientExitType("auth-request-cancelled")
  case AlreadyRespondedToAuthorisationRequest extends ClientExitType("already-responded-auth-request")
  case NotFound extends ClientExitType("not-found")

  override def toString: String = name
