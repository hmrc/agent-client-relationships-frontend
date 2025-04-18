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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.client.{ClientType, ExistingMainAgent, InvitationStatus}

import java.time.Instant

case class ValidateInvitationResponse(invitationId: String,
                                      serviceKey: String,
                                      agentName: String,
                                      status: InvitationStatus,
                                      lastModifiedDate: Instant,
                                      existingMainAgent: Option[ExistingMainAgent],
                                      clientType: Option[ClientType]
                                     )

object ValidateInvitationResponse {
  implicit val format: OFormat[ValidateInvitationResponse] = Json.format[ValidateInvitationResponse]
}

sealed trait ValidateInvitationError
case object InvitationAgentSuspendedError extends ValidateInvitationError
case object InvitationOrAgentNotFoundError extends ValidateInvitationError
