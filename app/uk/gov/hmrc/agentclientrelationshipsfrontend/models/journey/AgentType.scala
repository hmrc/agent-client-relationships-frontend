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

object AgentType:
  val mapping: Map[String, AgentType] = Map(
    "Main" -> AgentType.Main,
    "Supporting" -> AgentType.Supporting
  )

  val inverseMapping: Map[AgentType, String] = mapping.map(_.swap)

  implicit val agentTypeReads: Reads[AgentType] = Reads[AgentType] { json =>
    json.validate[String].flatMap(string => mapping
      .get(string).map(JsSuccess(_))
      .getOrElse(JsError("Invalid AgentType"))
    )
  }

  implicit val agentTypeWrites: Writes[AgentType] = Writes[AgentType] { agentType =>
    JsString(inverseMapping(agentType))
  }

  implicit val agentTypeFormat: Format[AgentType] = Format(agentTypeReads, agentTypeWrites)
  


