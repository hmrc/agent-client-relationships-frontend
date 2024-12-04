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

package uk.gov.hmrc.agentclientrelationshipsfrontend.services

import uk.gov.hmrc.agentclientrelationshipsfrontend.connectors.AgentClientRelationshipsConnector
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{AgentDetails, AuthorisationRequestInfo, ClientDetailsResponse}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.{AgentJourneyRequest, AgentJourney}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AgentClientRelationshipsService @Inject()(agentClientRelationshipsConnector: AgentClientRelationshipsConnector) {

  def getClientDetails(clientId: String, journey: AgentJourney)(implicit hc: HeaderCarrier): Future[Option[ClientDetailsResponse]] =
    agentClientRelationshipsConnector.getClientDetails(journey.getService, clientId)
    
  def getClientDetails(clientId: String, service: String)(implicit hc: HeaderCarrier): Future[Option[ClientDetailsResponse]] =
    agentClientRelationshipsConnector.getClientDetails(service, clientId)

  def createAuthorisationRequest(journey: AgentJourney)(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[String] = {
    agentClientRelationshipsConnector.createAuthorisationRequest(journey)
  }

  def cancelAuthorisation(journey: AgentJourney)(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[Unit] = {
    agentClientRelationshipsConnector.cancelAuthorisation(journey)
  }

  def getAuthorisationRequest(invitationId: String)(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[Option[AuthorisationRequestInfo]] = {
    agentClientRelationshipsConnector.getAuthorisationRequest(invitationId)
  }

  def getAgentDetails()(implicit hc: HeaderCarrier, request: AgentJourneyRequest[?]): Future[Option[AgentDetails]] = {
    agentClientRelationshipsConnector.getAgentDetails()
  }

}
