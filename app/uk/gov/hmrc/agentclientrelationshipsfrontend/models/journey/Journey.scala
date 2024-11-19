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

case class Journey(journeyType: JourneyType,
                   clientType: Option[String] = None,
                   clientService: Option[String] = None,
                   clientId: Option[String] = None,
                   clientDetailsResponse: Option[ClientDetailsResponse] = None,
                   knownFact: Option[String] = None,
                   agentType: Option[String] = None,
                   clientConfirmed: Option[Boolean] = None,
                   refinedService: Option[Boolean] = None):

  def getClientTypeWithDefault: String = clientType.getOrElse("")
  def getClientType: String = clientType.getOrElse(throw new RuntimeException("clientType not defined"))

  def getServiceWithDefault: String = clientService.getOrElse("")
  def getService: String = clientService.getOrElse(throw new RuntimeException("service not defined"))

  def getClientDetailsResponse: ClientDetailsResponse = clientDetailsResponse.getOrElse(throw new RuntimeException("client details are not defined"))

  def getKnownFactType: KnownFactType = clientDetailsResponse.flatMap(_.knownFactType)getOrElse(throw new RuntimeException("known fact is not defined"))

  def getExitType(journeyType: JourneyType, clientDetails: ClientDetailsResponse): Option[JourneyExitType] = journeyType match
    case JourneyType.AuthorisationRequest => clientDetails match {
      case ClientDetailsResponse(_, Some(ClientStatus.Insolvent), _, _, _, _, _) => Some(JourneyExitType.ClientStatusInsolvent)
      case ClientDetailsResponse(_, Some(_), _, _, _, _, _) => Some(JourneyExitType.ClientStatusInvalid)
      case ClientDetailsResponse(_, None, _, _, _, true, _) => Some(JourneyExitType.ClientAlreadyInvited)
      case ClientDetailsResponse(_, None, _, _, _, false, Some(service)) if service == clientService.get => Some(JourneyExitType.AuthorisationAlreadyExists)
      case ClientDetailsResponse(_, None, _, _, _, false, _) => None
    }
    case JourneyType.AgentCancelAuthorisation => clientDetails match {
      case ClientDetailsResponse(_, _, _, _, _, _, Some(service)) if service == clientService.get => None
      case ClientDetailsResponse(_, _, _, _, _, _, Some(_)) => Some(JourneyExitType.NoAuthorisationExists)
      case ClientDetailsResponse(_, _, _, _, _, _, None) => Some(JourneyExitType.NoAuthorisationExists)
    }

object Journey:
  implicit lazy val format: OFormat[Journey] = Json.format[Journey]


