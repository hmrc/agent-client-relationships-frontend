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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.client

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.Invitation

import java.time.LocalDate

case class ManageYourTaxAgentsData(
                                    agentsInvitations: AgentsInvitationsResponse,
                                    agentsAuthorisations: AgentsAuthorisationsResponse,
                                    authorisationEvents: AuthorisationEventsResponse
                    )
object ManageYourTaxAgentsData {
  implicit val format: OFormat[ManageYourTaxAgentsData] = Json.format[ManageYourTaxAgentsData]
}

case class AgentsInvitationsResponse(agentsInvitations: Seq[AgentData])

object AgentsInvitationsResponse {
  implicit val format: OFormat[AgentsInvitationsResponse] = Json.format[AgentsInvitationsResponse]
}

case class AgentsAuthorisationsResponse(agentsAuthorisations: Seq[AuthorisedAgent])

object AgentsAuthorisationsResponse {
  implicit val format: OFormat[AgentsAuthorisationsResponse] = Json.format[AgentsAuthorisationsResponse]
}

case class AuthorisationEventsResponse(authorisationEvents: Seq[AuthorisationEvent])

object AuthorisationEventsResponse {
  implicit val format: OFormat[AuthorisationEventsResponse] = Json.format[AuthorisationEventsResponse]
}

case class AgentData(uid: String, agentName: String, invitations: Seq[Invitation])

object AgentData {
  implicit val format: OFormat[AgentData] = Json.format[AgentData]
}

case class AuthorisedAgent(agentName: String, arn: String, authorisations: Seq[Authorisation])

object AuthorisedAgent {
  implicit val format: OFormat[AuthorisedAgent] = Json.format[AuthorisedAgent]
}

case class Authorisation(uid: String, service: String, clientId: String, date: LocalDate, arn: String, agentName: String)

object Authorisation {
  implicit val format: OFormat[Authorisation] = Json.format[Authorisation]
}

case class AuthorisationEvent(agentName: String, service: String, date: LocalDate, eventType: InvitationStatus)

object AuthorisationEvent {
  implicit val format: OFormat[AuthorisationEvent] = Json.format[AuthorisationEvent]
}

case class AuthorisationsCache(authorisations: Seq[Authorisation])

object AuthorisationsCache {
  implicit val format: OFormat[AuthorisationsCache] = Json.format[AuthorisationsCache]
}
