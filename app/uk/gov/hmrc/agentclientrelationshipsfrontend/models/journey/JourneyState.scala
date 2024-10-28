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


//TODO - review Val mapping and see if can be done with case class
enum JourneyState:
  case SelectClientType
  case SelectService
  case EnterClientId
  case EnterKnowFacts
  case ConfirmClient
  case SelectAgentType
  case CheckYourAnswers
  case Finished
  case Error(journeyErrors: JourneyErrorType)

object JourneyState {
  implicit val journeyStateReads: Reads[JourneyState] = Reads[JourneyState] { json =>
    (json \ "type").validate[String].flatMap {
      case "SelectClientType" => JsSuccess(JourneyState.SelectClientType)
      case "SelectService" => JsSuccess(JourneyState.SelectService)
      case "EnterClientId" => JsSuccess(JourneyState.EnterClientId)
      case "EnterKnowFacts" => JsSuccess(JourneyState.EnterKnowFacts)
      case "ConfirmClient" => JsSuccess(JourneyState.ConfirmClient)
      case "SelectAgentType" => JsSuccess(JourneyState.SelectAgentType)
      case "CheckYourAnswers" => JsSuccess(JourneyState.CheckYourAnswers)
      case "Finished" => JsSuccess(JourneyState.Finished)
      case "Error" => (json \ "journeyErrors").validate[JourneyErrorType].map(JourneyState.Error.apply)
      case _ => JsError("Invalid JourneyState")
    }
  }

  implicit val journeyStateWrites: Writes[JourneyState] = Writes[JourneyState] {
    case JourneyState.SelectClientType => Json.obj("type" -> "SelectClientType")
    case JourneyState.SelectService => Json.obj("type" -> "SelectService")
    case JourneyState.EnterClientId => Json.obj("type" -> "EnterClientId")
    case JourneyState.EnterKnowFacts => Json.obj("type" -> "EnterKnowFacts")
    case JourneyState.ConfirmClient => Json.obj("type" -> "ConfirmClient")
    case JourneyState.SelectAgentType => Json.obj("type" -> "SelectAgentType")
    case JourneyState.CheckYourAnswers => Json.obj("type" -> "CheckYourAnswers")
    case JourneyState.Finished => Json.obj("type" -> "Finished")
    case JourneyState.Error(journeyErrors: JourneyErrorType) => Json.obj("type" -> "Error", "journeyErrors" -> journeyErrors)
  }

  implicit val journeyStateFormat: Format[JourneyState] = Format(journeyStateReads, journeyStateWrites)
}
