/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.libs.json.*

enum IvResult:
  case Success, Incomplete, PreconditionFailed, LockedOut, InsufficientEvidence, FailedMatching, TechnicalIssue,
  UserAborted, TimedOut, FailedIv, FailedDirectorCheck

object IvResult:
  val mapping: Map[String, IvResult] = Map(
    "Success" -> Success,
    "Incomplete" -> Incomplete,
    "PreconditionFailed" -> PreconditionFailed,
    "LockedOut" -> LockedOut,
    "InsufficientEvidence" -> InsufficientEvidence,
    "FailedMatching" -> FailedMatching,
    "TechnicalIssue" -> TechnicalIssue,
    "UserAborted" -> UserAborted,
    "Timeout" -> TimedOut,
    "FailedIV" -> FailedIv,
    "FailedDirectorCheck" -> FailedDirectorCheck,
  )
  val inverseMapping: Map[IvResult, String] = mapping.map(_.swap)

  implicit val reads: Reads[IvResult] = Reads[IvResult] { json =>
    json.validate[String].flatMap(string => mapping
      .get(string).map(JsSuccess(_))
      .getOrElse(JsError(s"invalid IVResult value found: $string"))
    )
  }

  implicit val writes: Writes[IvResult] = Writes[IvResult] { ivResult =>
    JsString(inverseMapping(ivResult))
  }


