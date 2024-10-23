/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.fastTrack

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService

case class AgentFastTrackRequest(clientType: Option[String], service: String, clientId: String, knownFact: Option[String])

object AgentFastTrackRequest {

  //Field validators do not test dependencies between fields
  private def clientTypeReads(clientTypes: Set[String]): Reads[String] =
    Reads.StringReads.filter(JsonValidationError("UNSUPPORTED_CLIENT_TYPE"))(clientTypes.contains)

  private def serviceReads(services: Set[String]): Reads[String] =
    Reads.StringReads.filter(JsonValidationError("UNSUPPORTED_SERVICE"))(services.contains)

  private def clientIdentifierTypeReads(clientIdTypes: Set[String]): Reads[String] =
    Reads.StringReads.filter(JsonValidationError("UNSUPPORTED_CLIENT_ID_TYPE"))(clientIdTypes.contains)

  private def clientIdentifierReads(clientIdRegs: Set[String]): Reads[String] =
    Reads.StringReads.filter(JsonValidationError("INVALID_CLIENT_ID_RECEIVED"))(clientId => clientIdRegs.exists(x => clientId.matches(x)))

  //TODO WG - NEW ERROR CODE
  private def knownFactReads(knownFactRegs: Set[String]): Reads[String] =
    Reads.StringReads.filter(JsonValidationError("INVALID_KNOWN_FACT_RECEIVED"))(knowFact => knownFactRegs.exists(x => knowFact.matches(x)))

  def readsWithServiceConfigValidation(clientServiceConfig: ClientServiceConfigurationService): Reads[AgentFastTrackRequest] = (
    (JsPath \ "clientType").readNullable[String](clientTypeReads(clientServiceConfig.allClientTypes)) and
      (JsPath \ "service").read[String](serviceReads(clientServiceConfig.allServices)) and
      (JsPath \ "clientIdentifierType").read[String](clientIdentifierTypeReads(clientServiceConfig.allClientIdTypes)) and
      (JsPath \ "clientIdentifier").read[String](clientIdentifierReads(clientServiceConfig.allClientIdRegs)) and
      (JsPath \ "knownFact").readNullable[String](knownFactReads(clientServiceConfig.allKnowFactsRegs))
    ) { (clientType, service, clientIdentifierType, clientIdentifier, knownFact) =>
    AgentFastTrackRequest(clientType, service, clientIdentifier, knownFact)
  }.filter(JsonValidationError("INVALID_SUBMISSION")) { agentFastTrackRequest =>
    val serviceForClientType = agentFastTrackRequest.clientType.fold(true)(clientType => clientServiceConfig.clientServicesFor(clientType).contains(agentFastTrackRequest.service))
    val clientIdMatchRegs = agentFastTrackRequest.clientId.matches(clientServiceConfig.firstClientDetailsFieldFor(agentFastTrackRequest.service).regex)
    val knowFactMatchRegs = agentFastTrackRequest.knownFact.fold(true)(_.matches(clientServiceConfig.lastClientDetailsFieldFor(agentFastTrackRequest.service).regex))
    (serviceForClientType && clientIdMatchRegs && knowFactMatchRegs)
  }

  implicit val writes: OWrites[AgentFastTrackRequest] = Json.writes[AgentFastTrackRequest]

}
