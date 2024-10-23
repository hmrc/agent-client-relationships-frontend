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

import play.api.libs.json._
import play.api.mvc.PathBindable


enum JourneyType:
  case AuthorisationRequest, AgentCancelAuthorisation

  override def toString: String = this match {
    case AuthorisationRequest => "authorisation-request"
    case AgentCancelAuthorisation => "agentCancel-authorisation"
  }


implicit val journeyTypeReads: Reads[JourneyType] = Reads[JourneyType] { json =>
  json.validate[String].flatMap {
    case "AuthorisationRequest" => JsSuccess(JourneyType.AuthorisationRequest)
    case "AgentCancelAuthorisation" => JsSuccess(JourneyType.AgentCancelAuthorisation)
    case _ => JsError("Invalid JourneyType")
  }
}

implicit val journeyTypeWrites: Writes[JourneyType] = Writes[JourneyType] { journeyType =>
  JsString(journeyType.toString)
}

implicit val journeyTypeFormat: Format[JourneyType] = Format(journeyTypeReads, journeyTypeWrites)
