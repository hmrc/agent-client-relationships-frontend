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


enum JourneyErrors:
  case ClientNotFount, NotSignedUp, NotAuthorised, ClientNotRegistered, ClientInsolvent, ClientAlreadyAuthorised

implicit val journeyErrorsReads: Reads[JourneyErrors] = Reads[JourneyErrors] { json =>
  json.validate[String].flatMap {
    case "ClientNotFount" => JsSuccess(JourneyErrors.ClientNotFount)
    case "NotSignedUp" => JsSuccess(JourneyErrors.NotSignedUp)
    case "NotAuthorised" => JsSuccess(JourneyErrors.NotAuthorised)
    case "ClientNotRegistered" => JsSuccess(JourneyErrors.ClientNotRegistered)
    case "ClientInsolvent" => JsSuccess(JourneyErrors.ClientInsolvent)
    case "ClientAlreadyAuthorised" => JsSuccess(JourneyErrors.ClientAlreadyAuthorised)
    case _ => JsError("Invalid JourneyErrors")
  }
}

implicit val journeyErrorsWrites: Writes[JourneyErrors] = Writes[JourneyErrors] { journeyErrors =>
  JsString(journeyErrors.toString)
}

implicit val journeyErrorsFormat: Format[JourneyErrors] = Format(journeyErrorsReads, journeyErrorsWrites)
