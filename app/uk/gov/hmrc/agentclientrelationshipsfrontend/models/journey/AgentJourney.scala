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

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.{ClientDetailsResponse, ClientStatus, KnownFactType}

case class AgentJourney(journeyType: AgentJourneyType,
                        clientType: Option[String] = None,
                        clientService: Option[String] = None,
                        clientId: Option[String] = None,
                        clientDetailsResponse: Option[ClientDetailsResponse] = None,
                        knownFact: Option[String] = None,
                        agentType: Option[String] = None,
                        clientConfirmed: Option[Boolean] = None,
                        refinedService: Option[Boolean] = None,
                        alreadyManageAuth: Option[Boolean] = None,
                        abortMapping: Option[Boolean] = None,
                        journeyComplete: Option[String] = None,
                        confirmationClientName: Option[String] = None,
                        confirmationService: Option[String] = None,
                        backendErrorResponse: Option[Boolean] = None
                  ):

  def getClientTypeWithDefault: String = clientType.getOrElse("")
  def getClientType: String = clientType.getOrElse(throw new RuntimeException("clientType not defined"))

  def getServiceWithDefault: String = clientService.getOrElse("")
  def getService: String = clientService.getOrElse(throw new RuntimeException("service not defined"))
  def getClientId: String = clientId.getOrElse(throw new RuntimeException("clientId not defined"))
  def getClientDetailsResponse: ClientDetailsResponse = clientDetailsResponse.getOrElse(throw new RuntimeException("client details are not defined"))
  def getActiveRelationship: String = clientDetailsResponse.flatMap(_.hasExistingRelationshipFor).getOrElse(throw new RuntimeException("active relationship does not exist"))

  def getKnownFactType: KnownFactType = clientDetailsResponse.flatMap(_.knownFactType).getOrElse(throw new RuntimeException("known fact is not defined"))

  def getExitType(journeyType: AgentJourneyType, clientDetails: ClientDetailsResponse, supportedAgentRoles: Seq[String] = Seq.empty): Option[JourneyExitType] = journeyType match
    case AgentJourneyType.AuthorisationRequest => clientDetails match {
      case ClientDetailsResponse(_, Some(ClientStatus.Insolvent), _, _, _, _, _, _, _) => Some(JourneyExitType.ClientStatusInsolvent)
      case ClientDetailsResponse(_, Some(_), _, _, _, _, _, _, _) => Some(JourneyExitType.ClientStatusInvalid)
      case ClientDetailsResponse(_, None, _, _, _, true, _, _, _) => Some(JourneyExitType.ClientAlreadyInvited)
      case ClientDetailsResponse(_, None, _, _, _, false, Some(service), _, _) if supportedAgentRoles.isEmpty && service == clientService.get => Some(JourneyExitType.AuthorisationAlreadyExists)
      case ClientDetailsResponse(_, None, _, _, _, false, None, Some(true), Some(_)) if isMainAgent => Some(JourneyExitType.ClientAlreadyMapped)
      case ClientDetailsResponse(_, None, _, _, _, false, _, _, _) => None
    }
    case AgentJourneyType.AgentCancelAuthorisation => clientDetails match {
      case ClientDetailsResponse(_, _, _, _, _, _, Some(service), _, _) if service == clientService.get || supportedAgentRoles.contains(service) => None
      case ClientDetailsResponse(_, _, _, _, _, _, Some(_), _, _) => Some(JourneyExitType.NoAuthorisationExists)
      case ClientDetailsResponse(_, _, _, _, _, _, None, _, _) => Some(JourneyExitType.NoAuthorisationExists)
    }

      
  def isKnownFactValid: Boolean = clientDetailsResponse.exists { cdr =>
    cdr.knownFactType.fold(true) { _ =>
      knownFact.fold(cdr.knownFacts.isEmpty)(cdr.knownFacts.contains)
    }
  }

  def isMainAgent: Boolean = agentType.contains(getService)
  def eligibleForMapping: Boolean = isMainAgent && getClientDetailsResponse.isMapped.contains(false) && getClientDetailsResponse.clientsLegacyRelationships.exists(_.nonEmpty)


object AgentJourney:
  implicit lazy val format: OFormat[AgentJourney] = Json.format[AgentJourney]


