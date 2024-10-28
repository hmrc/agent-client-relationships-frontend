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


enum CheckYourAnswersResult:
  case BackToJourneyStart, UpdateAgentType, GoToNextPage

object CheckYourAnswersResult {
  implicit val checkYourAnswersResultReads: Reads[CheckYourAnswersResult] = Reads[CheckYourAnswersResult] { json =>
    json.validate[String].flatMap {
      case "BackToJourneyStart" => JsSuccess(CheckYourAnswersResult.BackToJourneyStart)
      case "UpdateAgentType" => JsSuccess(CheckYourAnswersResult.UpdateAgentType)
      case "GoToNextPage" => JsSuccess(CheckYourAnswersResult.GoToNextPage)
      case _ => JsError("Invalid CheckYourAnswersResult")
    }
  }

  implicit val checkYourAnswersResultWrites: Writes[CheckYourAnswersResult] = Writes[CheckYourAnswersResult] { checkYourAnswersResult =>
    JsString(checkYourAnswersResult.toString)
  }

  implicit val checkYourAnswersResultFormat: Format[CheckYourAnswersResult] = Format(checkYourAnswersResultReads, checkYourAnswersResultWrites)
}
