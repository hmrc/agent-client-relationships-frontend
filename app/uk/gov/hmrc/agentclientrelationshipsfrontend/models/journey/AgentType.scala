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


enum AgentType:
  case Main, Supporting

implicit val agentTypeReads: Reads[AgentType] = Reads[AgentType] { json =>
  json.validate[String].flatMap {
    case "Main" => JsSuccess(AgentType.Main)
    case "Supporting" => JsSuccess(AgentType.Supporting)
    case _ => JsError("Invalid AgentType")
  }
}

implicit val agentTypeWrites: Writes[AgentType] = Writes[AgentType] { agentType =>
  JsString(agentType.toString)
}

implicit val agentTypeFormat: Format[AgentType] = Format(agentTypeReads, agentTypeWrites)
