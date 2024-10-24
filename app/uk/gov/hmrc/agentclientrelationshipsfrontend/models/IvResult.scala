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

import play.api.libs.json._
import uk.gov.hmrc.http.BadRequestException

sealed trait IvResult {
  val value: String
}

case object Success extends IvResult { override val value = "Success" }
case object Incomplete extends IvResult { override val value = "Incomplete" }
case object PreconditionFailed extends IvResult { override val value = "PreconditionFailed" }
case object LockedOut extends IvResult { override val value = "LockedOut" }
case object InsufficientEvidence extends IvResult { override val value = "InsufficientEvidence" }
case object FailedMatching extends IvResult { override val value = "FailedMatching" }
case object TechnicalIssue extends IvResult { override val value = "TechnicalIssue" }
case object UserAborted extends IvResult { override val value = "UserAborted" }
case object TimedOut extends IvResult { override val value = "Timeout" }
case object FailedIv extends IvResult { override val value = "FailedIV" }
case object FailedDirectorCheck extends IvResult { override val value = "FailedDirectorCheck" }

object IvResult {
  def apply(str: String): IvResult = str match {
    case Success.value              => Success
    case Incomplete.value           => Incomplete
    case PreconditionFailed.value   => PreconditionFailed
    case LockedOut.value            => LockedOut
    case InsufficientEvidence.value => InsufficientEvidence
    case FailedMatching.value       => FailedMatching
    case TechnicalIssue.value       => TechnicalIssue
    case UserAborted.value          => UserAborted
    case TimedOut.value             => TimedOut
    case FailedIv.value             => FailedIv
    case FailedDirectorCheck.value  => FailedDirectorCheck
    case _                          => throw new BadRequestException("strange value for IVReason")
  }

  def unapply(reason: IvResult): Option[String] = reason match {
    case Success              => Some("Success")
    case Incomplete           => Some("Incomplete")
    case PreconditionFailed   => Some("PreconditionFailed")
    case LockedOut            => Some("LockedOut")
    case InsufficientEvidence => Some("InsufficientEvidence")
    case FailedMatching       => Some("FailedMatching")
    case TechnicalIssue       => Some("TechnicalIssue")
    case UserAborted          => Some("UserAborted")
    case TimedOut             => Some("Timeout")
    case FailedIv             => Some("FailedIV")
    case FailedDirectorCheck  => Some("FailedDirectorCheck")
  }

  implicit val reads: Reads[IvResult] = new Reads[IvResult] {
    override def reads(json: JsValue): JsResult[IvResult] =
      json match {
        case JsString(Success.value)              => JsSuccess(Success)
        case JsString(Incomplete.value)           => JsSuccess(Incomplete)
        case JsString(PreconditionFailed.value)   => JsSuccess(PreconditionFailed)
        case JsString(LockedOut.value)            => JsSuccess(LockedOut)
        case JsString(InsufficientEvidence.value) => JsSuccess(InsufficientEvidence)
        case JsString(FailedMatching.value)       => JsSuccess(FailedMatching)
        case JsString(TechnicalIssue.value)       => JsSuccess(TechnicalIssue)
        case JsString(UserAborted.value)          => JsSuccess(UserAborted)
        case JsString(TimedOut.value)             => JsSuccess(TimedOut)
        case JsString(FailedIv.value)             => JsSuccess(FailedIv)
        case JsString(FailedDirectorCheck.value)  => JsSuccess(FailedDirectorCheck)
        case invalid                              => JsError(s"invalid IVResult value found: $invalid")
      }
  }

  implicit val writes: Writes[IvResult] = new Writes[IvResult] {
    override def writes(o: IvResult): JsValue = JsString(o.value)
  }

}
