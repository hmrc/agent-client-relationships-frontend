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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.test

import play.api.data.*
import play.api.data.Forms.*
import play.api.data.format.Formats.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.forms.helpers.FormFieldHelper
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.fastTrack.AgentFastTrackRequest

object TestFastTrackForm extends FormFieldHelper {
  val normalizedText: Mapping[String] = of[String].transform(_.replaceAll("\\s", ""), identity)
  
  //TODO WG - add validation for clientIdentifierType
  //TODO WG - add validation for clientIdentifier
  //TODO WG - add validation for knownFact
  def form(clientTypes: Set[String], services: Set[String]): Form[AgentFastTrackRequest] = {
    Form(
      mapping(
        "clientType"            -> optional(text),//.verifying( _.fold(true)(clientTypes.contains)),
        "service"               -> text,//.verifying(services.contains),
        "clientIdentifierType"  -> text,
        "clientIdentifier"      -> text,//normalizedText,
        "knownFact"             -> optional(text)
      ) { (clientType, service, clientIdType, clientId, knownFact) =>
        println(s"${Console.MAGENTA} Wojciech TestFastTrackForm validation OK ${Console.RESET}")
        AgentFastTrackRequest(clientType, service, clientId, knownFact)
      } { fastTrack =>
        Some((fastTrack.clientType, fastTrack.service, fastTrack.clientId, fastTrack.clientId, fastTrack.knownFact))
      }
    )
  }
}
