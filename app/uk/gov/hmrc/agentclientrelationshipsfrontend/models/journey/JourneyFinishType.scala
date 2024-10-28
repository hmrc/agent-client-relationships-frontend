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


//TODO WG - In case we need mor
enum JourneyFinishType:
  case InvitationCreated, AuthorisationCancelled,ClientAlreadyAuthorised

implicit val journeyFinishTypeReads: Reads[JourneyFinishType] = Reads[JourneyFinishType] { json =>
  json.validate[String].flatMap {
    case "InvitationCreated" => JsSuccess(JourneyFinishType.InvitationCreated)
    case "AuthorisationCancelled" => JsSuccess(JourneyFinishType.AuthorisationCancelled)
    case "ClientAlreadyAuthorised" => JsSuccess(JourneyFinishType.ClientAlreadyAuthorised)
    case _ => JsError("Invalid JourneyFinishType")
  }
}

implicit val journeyFinishTypeWrites: Writes[JourneyFinishType] = Writes[JourneyFinishType] { journeyFinishType =>
  JsString(journeyFinishType.toString)
}

implicit val journeyFinishTypeFormat: Format[JourneyFinishType] = Format(journeyFinishTypeReads, journeyFinishTypeWrites)
