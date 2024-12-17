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

import play.api.libs.json.*
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.AgentJourneyType.inverseMapping

enum AgentJourneyType:
  case AuthorisationRequest, AgentCancelAuthorisation

  override def toString: String = inverseMapping(this)

object AgentJourneyType:
  val mapping: Map[String, AgentJourneyType] = Map(
    "authorisation-request" -> AuthorisationRequest,
    "agent-cancel-authorisation" -> AgentCancelAuthorisation
  )
  val inverseMapping: Map[AgentJourneyType, String] = mapping.map(_.swap)

  implicit val journeyTypeReads: Reads[AgentJourneyType] = Reads[AgentJourneyType] { json =>
    json.validate[String].flatMap(string => mapping
      .get(string).map(JsSuccess(_))
      .getOrElse(JsError("Invalid JourneyType"))
    )
  }

  implicit val journeyTypeWrites: Writes[AgentJourneyType] = Writes[AgentJourneyType] { journeyType =>
    JsString(inverseMapping(journeyType))
  }

  implicit val journeyTypeFormat: Format[AgentJourneyType] = Format(journeyTypeReads, journeyTypeWrites)
