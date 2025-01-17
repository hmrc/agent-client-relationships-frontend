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

enum JourneyState:
  case SelectClientType, SelectService, EnterClientId

object JourneyState:
  val mapping: Map[String, JourneyState] = Map(
    "SelectClientType" -> JourneyState.SelectClientType,
    "SelectService" -> JourneyState.SelectService,
    "EnterClientId" -> JourneyState.EnterClientId
  )
  val inverseMapping: Map[JourneyState, String] = mapping.map(_.swap)

  implicit val journeyStateReads: Reads[JourneyState] = Reads[JourneyState] { json =>
    json.validate[String].flatMap(string => mapping
      .get(string).map(JsSuccess(_))
      .getOrElse(JsError("Invalid JourneyState"))
    )
  }

  implicit val journeyStateWrites: Writes[JourneyState] = Writes[JourneyState] { journeyState =>
    JsString(inverseMapping(journeyState))
  }

  implicit val journeyStateFormat: Format[JourneyState] = Format(journeyStateReads, journeyStateWrites)
