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

package uk.gov.hmrc.agentclientrelationshipsfrontend.binders

import play.api.mvc.PathBindable
import uk.gov.hmrc.agentclientrelationshipsfrontend.models.journey.JourneyType

object UrlBinders {
  implicit val journeyTypeBinder: PathBindable[JourneyType] = getJourneyTypeBinder

  private def getJourneyTypeBinder(implicit stringBinder: PathBindable[String]) = new PathBindable[JourneyType] {

    override def bind(key: String, value: String): Either[String, JourneyType] = value match
      case "authorisation-request" => Right(JourneyType.AuthorisationRequest)
      case "agentCancel-authorisation" => Right(JourneyType.AgentCancelAuthorisation)
      case other => Left(s"Invalid value for $key: $other")

    override def unbind(key: String, value: JourneyType): String =
      value.toString
  }

}
