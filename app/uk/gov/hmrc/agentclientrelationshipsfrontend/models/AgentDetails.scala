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

package uk.gov.hmrc.agentclientrelationshipsfrontend.models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AgentDetails(
  agencyName: String,
  agencyEmail: String
)

object AgentDetails {

  private def optionalReads(fieldName: String): Reads[String] = Reads[String] {
    case JsString(value) => JsSuccess(value)
    case JsNull          => JsError(s"$fieldName must not be null")
    case _               => JsError(s"Invalid $fieldName value")
  }

  private val reads: Reads[AgentDetails] = (
    (__ \ "agencyDetails" \ "agencyName").read(optionalReads("Agency name")).orElse(Reads.pure("")) and
      (__ \ "agencyDetails" \ "agencyEmail").read(optionalReads("Agency email")).orElse(Reads.pure(""))
  )(AgentDetails.apply _)

  private val writes: Writes[AgentDetails] = Json.writes[AgentDetails]

  implicit val agencyDetailsFormat: Format[AgentDetails] = Format(reads, writes)

}