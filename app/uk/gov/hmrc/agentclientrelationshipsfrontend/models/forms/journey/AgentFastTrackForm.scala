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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.journey

import play.api.data.Forms.*
import play.api.data.format.Formats.*
import play.api.data.validation.*
import play.api.data.{Form, Mapping}
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.AgentFastTrackRequest
import uk.gov.hmrc.agentclientrelationshipsfrontend.services.ClientServiceConfigurationService

object AgentFastTrackForm {
  val lowerCaseText: Mapping[String] = of[String].transform(_.trim.toLowerCase, identity)
  val normalizedText: Mapping[String] = of[String].transform(_.replaceAll("\\s", ""), identity)
  val uppercaseNormalizedText: Mapping[String] = normalizedText.transform(_.toUpperCase, identity)

  def validateClientId(clientIdRegs: Set[String]): Constraint[String] = Constraint[String] { clientId =>
    val isValid = clientIdRegs.exists(x => clientId.matches(x))
    if (isValid) Valid else Invalid(ValidationError(s"INVALID_CLIENT_ID_RECEIVED:${if (clientId.nonEmpty) clientId else "NOTHING"}"))
  }


  def validateFastTrackForm(clientServiceConfig: ClientServiceConfigurationService): Constraint[AgentFastTrackRequest] =
    Constraint[AgentFastTrackRequest] { agentFastTrackRequest =>

      val clientIdMatchRegs = clientServiceConfig.clientDetailForServiceAndClientIdType(
        agentFastTrackRequest.service, 
        agentFastTrackRequest.clientIdentifierType)
        .fold(false)(x => agentFastTrackRequest.clientIdentifier.matches(x.regex))

      if (clientIdMatchRegs) Valid
      else Invalid(ValidationError("INVALID_SUBMISSION"))
    }

  def form(clientServiceConfig: ClientServiceConfigurationService): Form[AgentFastTrackRequest] = {
    Form(
      mapping(
        "service" -> text
          .verifying("UNSUPPORTED_SERVICE", clientServiceConfig.allSupportedServices.contains(_)),
        "clientIdentifierType" -> text
          .verifying("UNSUPPORTED_CLIENT_ID_TYPE", clientServiceConfig.allSupportedClientTypeIds.contains(_)),
        "clientIdentifier" -> uppercaseNormalizedText.verifying(validateClientId(clientServiceConfig.allClientIdRegex)),
        "knownFact"        -> optional(text)
      ) { (service, clientIdType, clientId, knownFact) =>
        AgentFastTrackRequest(service, clientId, clientIdType, knownFact)
      } { request =>
        Some((request.service, request.clientIdentifierType, request.clientIdentifier, request.knownFact))
      }.verifying(validateFastTrackForm(clientServiceConfig))
    )

  }
}
